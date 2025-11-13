package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.NothingAction;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Getter
public class CallbackPowerTickPower extends Power {

	public static final MapCodec<CallbackPowerTickPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Action.BASE_CODEC.optionalFieldOf("tick_action", new NothingAction()).forGetter(CallbackPowerTickPower::getTickAction))
		.and(Action.BASE_CODEC.optionalFieldOf("rising_action", new NothingAction()).forGetter(CallbackPowerTickPower::getRisingAction))
		.and(Action.BASE_CODEC.optionalFieldOf("falling_action", new NothingAction()).forGetter(CallbackPowerTickPower::getFallingAction))
		.and(NumberProvider.clamped(1, Integer.MAX_VALUE).optionalFieldOf("interval", new ConstantNumberProvider(20)).forGetter(CallbackPowerTickPower::getInterval))
		.apply(instance, CallbackPowerTickPower::new));

	public static final PacketCodec<RegistryByteBuf, CallbackPowerTickPower> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Condition.BASE_PACKET_CODEC), Power::getActiveCondition,
		Action.BASE_PACKET_CODEC, CallbackPowerTickPower::getTickAction,
		Action.BASE_PACKET_CODEC, CallbackPowerTickPower::getRisingAction,
		Action.BASE_PACKET_CODEC, CallbackPowerTickPower::getFallingAction,
		NumberProvider.PACKET_CODEC, CallbackPowerTickPower::getInterval,
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
	public PowerType<?> getType() {
		return PowerTypes.CALLBACK_POWER_TICK;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {

		super.validate(reporter);

		getTickAction().validate(reporter.makeChild(".tick_action"));
		getRisingAction().validate(reporter.makeChild(".rising_action"));
		getFallingAction().validate(reporter.makeChild(".falling_action"));
		getInterval().validate(reporter.makeChild(".interval"));

	}

	public static class Instance extends Power.Instance<CallbackPowerTickPower> {

		private Integer startTicks;
		private Integer endTicks;

		private boolean wasActive;

		protected Instance(@NotNull Entity holder, @NotNull CallbackPowerTickPower power) {
			super(holder, power);
		}

		@Override
		public void onTick() {

			Context context = createHolderContext();
			int interval = power.getInterval().nextInt(context.makeChild(".interval"));

			if (context.hasAnyErrors()) {

				this.startTicks = null;
				this.endTicks = null;

				this.wasActive = false;

			}

			else {

				int ticks = holder.age % interval;
				if (this.isActive(context)) {

					if (startTicks == null) {
						this.startTicks = ticks;
						this.endTicks = null;
					}

					else if (ticks == startTicks) {

						if (!wasActive) {
							power.getRisingAction().execute(context.makeChild(".rising_action"));
							wasActive = true;
						}

						else {
							power.getTickAction().execute(context.makeChild(".tick_action"));
						}

					}

				}

				else if (wasActive) {

					if (endTicks == null) {
						this.startTicks = null;
						this.endTicks = ticks;
					}

					else if (ticks == endTicks) {
						power.getFallingAction().execute(context.makeChild(".falling_action"));
						wasActive = false;
					}

				}

			}

		}

		@Override
		public boolean shouldTick() {
			return true;
		}

	}

}
