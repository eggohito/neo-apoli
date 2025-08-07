package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.entity.NothingEntityAction;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.meta.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

@Getter
public class TickingPower extends Power {

	public static final MapCodec<TickingPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(EntityAction.CODEC.optionalFieldOf("tick_action", new NothingEntityAction()).forGetter(TickingPower::getTickAction))
		.and(EntityAction.CODEC.optionalFieldOf("first_active_tick_action", new NothingEntityAction()).forGetter(TickingPower::getFirstActiveTickAction))
		.and(EntityAction.CODEC.optionalFieldOf("first_inactive_tick_action", new NothingEntityAction()).forGetter(TickingPower::getFirstInactiveTickAction))
		.and(NumberProvider.clamped(1, Integer.MAX_VALUE).optionalFieldOf("interval", new ConstantNumberProvider(20)).forGetter(TickingPower::getInterval))
		.apply(instance, TickingPower::new));

	public static final PacketCodec<RegistryByteBuf, TickingPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, tickingPower) -> {
			EntityAction.PACKET_CODEC.encode(buf, tickingPower.getTickAction());
			EntityAction.PACKET_CODEC.encode(buf, tickingPower.getFirstActiveTickAction());
			EntityAction.PACKET_CODEC.encode(buf, tickingPower.getFirstInactiveTickAction());
			NumberProvider.PACKET_CODEC.encode(buf, tickingPower.getInterval());
		},
		(buf, properties, condition) -> new TickingPower(properties, condition,
			EntityAction.PACKET_CODEC.decode(buf),
			EntityAction.PACKET_CODEC.decode(buf),
			EntityAction.PACKET_CODEC.decode(buf),
			NumberProvider.PACKET_CODEC.decode(buf)
		)
	);

	private final EntityAction tickAction;
	private final EntityAction firstActiveTickAction;
	private final EntityAction firstInactiveTickAction;

	private final NumberProvider interval;

	public TickingPower(Properties properties, EntityCondition activeCondition, EntityAction tickAction, EntityAction firstActiveTickAction, EntityAction firstInactiveTickAction, NumberProvider interval) {
		super(properties, activeCondition);
		this.tickAction = tickAction;
		this.firstActiveTickAction = firstActiveTickAction;
		this.firstInactiveTickAction = firstInactiveTickAction;
		this.interval = interval;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.TICKING;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {

		super.validate(reporter);

		getTickAction().validate(reporter.makeChild(".tick_action"));
		getFirstActiveTickAction().validate(reporter.makeChild(".first_active_tick_action"));
		getFirstInactiveTickAction().validate(reporter.makeChild(".first_inactive_tick_action"));
		getInterval().validate(reporter.makeChild(".interval"));

	}

	public static class Impl extends Power.Impl<TickingPower> {

		private Integer startTicks;
		private Integer endTicks;

		private boolean wasActive;

		protected Impl(@NotNull Entity holder, @NotNull TickingPower power) {
			super(holder, power);
		}

		@Override
		public void onTick() {

			Context context = this.createGenericContext();
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
							power.getFirstActiveTickAction().execute(context.makeChild(".first_active_tick_action"));
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
						power.getFirstInactiveTickAction().execute(context.makeChild(".first_inactive_tick_action"));
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
