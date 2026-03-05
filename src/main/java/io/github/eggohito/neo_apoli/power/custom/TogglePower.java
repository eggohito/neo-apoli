package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.NothingAction;
import io.github.eggohito.neo_apoli.api.key.KeyReference;
import io.github.eggohito.neo_apoli.condition.Condition;
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
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@EqualsAndHashCode
@Getter
public class TogglePower extends Power {

	public static final MapCodec<TogglePower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Action.CODEC.optionalFieldOf("action", NothingAction.INSTANCE).forGetter(TogglePower::getAction))
		.and(KeyReference.CODEC.fieldOf("key").forGetter(TogglePower::getKey))
		.and(BooleanProvider.CODEC.optionalFieldOf("retain_state", new ConstantBooleanProvider(true)).forGetter(TogglePower::getRetainState))
		.and(BooleanProvider.CODEC.optionalFieldOf("active_by_default", new ConstantBooleanProvider(true)).forGetter(TogglePower::getActiveByDefault))
		.apply(instance, TogglePower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, TogglePower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		Action.STREAM_CODEC, TogglePower::getAction,
		KeyReference.STREAM_CODEC, TogglePower::getKey,
		BooleanProvider.STREAM_CODEC, TogglePower::getRetainState,
		BooleanProvider.STREAM_CODEC, TogglePower::getActiveByDefault,
		TogglePower::new
	);

	private final Action action;
	private final KeyReference key;

	private final BooleanProvider retainState;
	private final BooleanProvider activeByDefault;

	public TogglePower(Optional<Condition> activeCondition, Action action, KeyReference key, BooleanProvider retainState, BooleanProvider activeByDefault) {
		super(activeCondition);
		this.action = action;
		this.key = key;
		this.retainState = retainState;
		this.activeByDefault = activeByDefault;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.TOGGLE;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	@Override
	public void validate(Context.Validator validator) {

		super.validate(validator);

		getAction().validate(validator.forChild(".action"));
		getKey().validate(validator.forChild(".key"));
		getRetainState().validate(validator.forChild(".retain_state"));
		getActiveByDefault().validate(validator.forChild(".active_by_default"));

	}

	public static class Instance extends Power.Instance<TogglePower> {

		private static final MapCodec<Boolean> TOGGLED_CODEC = Codec.BOOL.fieldOf("toggled");
		private boolean toggled;

		protected Instance(@NotNull TogglePower power) {
			super(power);
		}

		@Override
		public <I> DataResult<Unit> decodeData(DynamicOps<I> ops, MapLike<I> mapInput) {
			return TOGGLED_CODEC.decode(ops, mapInput)
				.ifSuccess(bool -> this.toggled = bool)
				.map(ignored -> Unit.INSTANCE);
		}

		@Override
		public <O> RecordBuilder<O> encodeData(DynamicOps<O> ops, RecordBuilder<O> prefix) {
			return TOGGLED_CODEC.encode(this.toggled, ops, prefix);
		}

		@Override
		public void onGranted(Entity holder) {
			this.toggled = power.getActiveByDefault().nextBoolean(this.createHolderContext(holder).forChild(".active_by_default"));
		}

		@Override
		public void onTick(Entity holder) {

			Context context = this.createHolderContext(holder);

			if (toggled && !super.isActive(context)) {
				this.toggle(holder, context);
			}

		}

		@Override
		public boolean shouldTick(Entity holder) {
			return power.getActiveCondition().isPresent()
				&& !power.getRetainState().nextBoolean(this.createHolderContext(holder).forChild(".retain_state"));
		}

		@Override
		public boolean isActive(Context context) {
			return toggled;
		}

		public KeyReference getKey() {
			return power.getKey();
		}

		public void toggle(Entity holder, Context context) {

			try {

				if (context.visitor().push(this)) {

					if (!holder.level().isClientSide()) {

						this.toggled = !toggled;
						this.syncData(holder);

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
