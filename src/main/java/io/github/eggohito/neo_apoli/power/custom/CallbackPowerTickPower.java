package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.NothingAction;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliPowerTypes;
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
public class CallbackPowerTickPower extends Power {

	public static final MapCodec<CallbackPowerTickPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Action.CODEC.optionalFieldOf("tick_action", NothingAction.INSTANCE).forGetter(CallbackPowerTickPower::getTickAction))
		.and(Action.CODEC.optionalFieldOf("rising_action", NothingAction.INSTANCE).forGetter(CallbackPowerTickPower::getRisingAction))
		.and(Action.CODEC.optionalFieldOf("falling_action", NothingAction.INSTANCE).forGetter(CallbackPowerTickPower::getFallingAction))
		.and(NumberProvider.clamped(0, Integer.MAX_VALUE).optionalFieldOf("interval", new ConstantNumberProvider(20)).forGetter(CallbackPowerTickPower::getInterval))
		.apply(instance, CallbackPowerTickPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, CallbackPowerTickPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		Action.STREAM_CODEC, CallbackPowerTickPower::getTickAction,
		Action.STREAM_CODEC, CallbackPowerTickPower::getRisingAction,
		Action.STREAM_CODEC, CallbackPowerTickPower::getFallingAction,
		NumberProvider.STREAM_CODEC, CallbackPowerTickPower::getInterval,
		CallbackPowerTickPower::new
	);

	private final Action tickAction;
	private final Action risingAction;
	private final Action fallingAction;
	private final NumberProvider interval;

	public CallbackPowerTickPower(Optional<Condition> activeCondition, Action tickAction, Action risingAction, Action fallingAction, NumberProvider interval) {
		super(activeCondition);
		this.tickAction = tickAction;
		this.risingAction = risingAction;
		this.fallingAction = fallingAction;
		this.interval = interval;
	}

	@Override
	public Type<?> getType() {
		return NeoApoliPowerTypes.CALLBACK_POWER_TICK;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	@Override
	public void validate(Context.Validator validator) {

		super.validate(validator);

		getTickAction().validate(validator.forChild(".tick_action"));
		getRisingAction().validate(validator.forChild(".rising_action"));
		getFallingAction().validate(validator.forChild(".falling_action"));
		getInterval().validate(validator.forChild(".interval"));

	}

	public static class Instance extends Power.Instance<CallbackPowerTickPower> {

		private Integer startTicks;
		private Integer endTicks;

		private boolean wasActive;

		protected Instance(@NotNull CallbackPowerTickPower power) {
			super(power);
		}

		@Override
		public void onTick(Entity holder) {

			Context context = createHolderContext(holder);
			int interval = power.getInterval().getInt(context.forChild(".interval"));

			if (context.hasAnyErrors()) {

				this.startTicks = null;
				this.endTicks = null;

				this.wasActive = false;

			}

			else {

				int ticks = interval > 0
					? holder.tickCount % interval
					: holder.tickCount;

				if (this.isActive(context)) {

					if (interval > 0 && startTicks == null) {
						this.startTicks = ticks;
						this.endTicks = null;
					}

					else if (interval <= 0 || ticks == startTicks) {

						if (!wasActive) {
							power.getRisingAction().execute(context.forChild(".rising_action"));
							this.wasActive = true;
						}

						else {
							power.getTickAction().execute(context.forChild(".tick_action"));
						}

					}

				}

				else if (wasActive) {

					if (interval > 0 && endTicks == null) {
						this.startTicks = null;
						this.endTicks = ticks;
					}

					else if (interval <= 0 || ticks == endTicks) {
						power.getFallingAction().execute(context.forChild(".falling_action"));
						this.wasActive = false;
					}

				}

			}

		}

		@Override
		public boolean shouldTick(Entity holder) {
			return true;
		}

	}

}
