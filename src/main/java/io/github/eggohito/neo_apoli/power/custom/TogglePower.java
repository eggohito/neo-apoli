package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.NothingAction;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.key.KeyCondition;
import io.github.eggohito.neo_apoli.key.KeyState;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Getter
public class TogglePower extends Power {

	public static final MapCodec<TogglePower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Action.CODEC.optionalFieldOf("action", new NothingAction()).forGetter(TogglePower::getAction))
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
		return new io.github.eggohito.neo_apoli.power.custom.TogglePower.Instance(holder, this);
	}

	public static class Instance extends Power.Instance<TogglePower> {

		private boolean toggled;

		protected Instance(@NotNull Entity holder, @NotNull TogglePower power) {
			super(holder, power);
			this.toggled = power.getActiveByDefault().next(this.createHolderContext().makeChild(".active_by_default"));
		}

		@Override
		public <I> DataResult<Unit> decodeData(RegistryOps<I> ops, I data) {
			return ops.getBooleanValue(data)
				.ifSuccess(bool -> this.toggled = bool)
				.map(bool -> Unit.INSTANCE);
		}

		@Override
		public <I> DataResult<I> encodeData(RegistryOps<I> ops) {
			return DataResult.success(ops.createBoolean(this.toggled));
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
				&& !power.getRetainState().next(this.createHolderContext().makeChild(".retain_state"));
		}

		@Override
		public boolean isActive(Context context) {
			return toggled;
		}

		public boolean shouldToggle(Context context) {
			return super.isActive(context)
				&& power.getKeyCondition().test(context.makeChild(".key_condition"));
		}

		public void toggle(Context context) {

			try {

				if (context.markActive(this)) {

					if (!holder.level().isClientSide()) {

						this.toggled = !toggled;
						this.syncData();

					}

					power.getAction().execute(context.makeChild(".action"));

				}

			}

			finally {
				context.markInActive(this);
			}

		}

	}

	public static void onKeyPressed(Player player, KeyState ignoredState) {

		for (var instance : PowersComponent.getInstances(player, io.github.eggohito.neo_apoli.power.custom.TogglePower.Instance.class)) {

			Context instanceContext = instance.createHolderContext();

			if (instance.shouldToggle(instanceContext)) {
				instance.toggle(instanceContext);
			}

		}

	}

}
