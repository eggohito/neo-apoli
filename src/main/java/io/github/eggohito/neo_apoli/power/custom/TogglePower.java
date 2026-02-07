package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.meta.NothingMetaAction;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.key.KeyCondition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@EqualsAndHashCode
@Getter
public class TogglePower extends Power {

	public static final MapCodec<TogglePower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Action.CODEC.optionalFieldOf("action", NothingMetaAction.INSTANCE).forGetter(TogglePower::getAction))
		.and(KeyCondition.CODEC.fieldOf("key_condition").forGetter(TogglePower::getKeyCondition))
		.and(BooleanProvider.CODEC.optionalFieldOf("retain_state", new ConstantBooleanProvider(true)).forGetter(TogglePower::getRetainState))
		.and(BooleanProvider.CODEC.optionalFieldOf("active_by_default", new ConstantBooleanProvider(true)).forGetter(TogglePower::getActiveByDefault))
		.apply(instance, TogglePower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, TogglePower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		Action.STREAM_CODEC, TogglePower::getAction,
		KeyCondition.STREAM_CODEC, TogglePower::getKeyCondition,
		BooleanProvider.STREAM_CODEC, TogglePower::getRetainState,
		BooleanProvider.STREAM_CODEC, TogglePower::getActiveByDefault,
		TogglePower::new
	);

	private final Action action;
	private final KeyCondition keyCondition;

	private final BooleanProvider retainState;
	private final BooleanProvider activeByDefault;

	public TogglePower(Optional<Condition> activeCondition, Action action, KeyCondition keyCondition, BooleanProvider retainState, BooleanProvider activeByDefault) {
		super(activeCondition);
		this.action = action;
		this.keyCondition = keyCondition;
		this.retainState = retainState;
		this.activeByDefault = activeByDefault;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.TOGGLE;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	public static class Instance extends Power.Instance<TogglePower> {

		private static final MapCodec<Boolean> DATA_CODEC = Codec.BOOL.fieldOf("toggled");
		private boolean toggled;

		protected Instance(@NotNull Entity holder, @NotNull TogglePower power) {
			super(holder, power);
		}

		@Override
		public <I> DataResult<Unit> decodeData(RegistryOps<I> ops, MapLike<I> mapInput) {
			return DATA_CODEC.decode(ops, mapInput)
				.ifSuccess(bool -> this.toggled = bool)
				.map(ignored -> Unit.INSTANCE);
		}

		@Override
		public <O> RecordBuilder<O> encodeData(RegistryOps<O> ops, RecordBuilder<O> prefix) {
			return DATA_CODEC.encode(this.toggled, ops, prefix);
		}

		@Override
		public void onGranted() {
			this.toggled = power.getActiveByDefault().next(this.createHolderContext().forChild(".active_by_default"));
		}

		@Override
		public void onTick() {

			Context context = this.createHolderContext();

			if (toggled && !super.isActive(context)) {
				this.toggle(context);
			}

		}

		@Override
		public boolean shouldTick() {
			return power.getActiveCondition().isPresent()
				&& !power.getRetainState().next(this.createHolderContext().forChild(".retain_state"));
		}

		@Override
		public boolean isActive(Context context) {
			return toggled;
		}

		public boolean shouldToggle(Context context) {
			return super.isActive(context)
				&& power.getKeyCondition().test(context.forChild(".key_condition"));
		}

		public void toggle(Context context) {

			try {

				if (context.visitor().push(this)) {

					if (!holder.level().isClientSide()) {

						this.toggled = !toggled;
						this.syncData();

					}

					power.getAction().execute(context.forChild(".action"));

				}

			}

			finally {
				context.visitor().pop(this);
			}

		}

	}

}
