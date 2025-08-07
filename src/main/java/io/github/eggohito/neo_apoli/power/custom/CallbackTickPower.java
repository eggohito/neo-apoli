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
public class CallbackTickPower extends Power {

	public static final MapCodec<CallbackTickPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(EntityAction.CODEC.optionalFieldOf("entity_action", new NothingEntityAction()).forGetter(CallbackTickPower::getEntityAction))
		.and(EntityAction.CODEC.optionalFieldOf("rising_action", new NothingEntityAction()).forGetter(CallbackTickPower::getRisingEntityAction))
		.and(EntityAction.CODEC.optionalFieldOf("falling_action", new NothingEntityAction()).forGetter(CallbackTickPower::getFallingEntityAction))
		.and(NumberProvider.clamped(1, Integer.MAX_VALUE).optionalFieldOf("interval", new ConstantNumberProvider(20)).forGetter(CallbackTickPower::getInterval))
		.apply(instance, CallbackTickPower::new));

	public static final PacketCodec<RegistryByteBuf, CallbackTickPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, callbackTickPower) -> {
			EntityAction.PACKET_CODEC.encode(buf, callbackTickPower.getEntityAction());
			EntityAction.PACKET_CODEC.encode(buf, callbackTickPower.getRisingEntityAction());
			EntityAction.PACKET_CODEC.encode(buf, callbackTickPower.getFallingEntityAction());
			NumberProvider.PACKET_CODEC.encode(buf, callbackTickPower.getInterval());
		},
		(buf, properties, condition) -> new CallbackTickPower(properties, condition,
			EntityAction.PACKET_CODEC.decode(buf),
			EntityAction.PACKET_CODEC.decode(buf),
			EntityAction.PACKET_CODEC.decode(buf),
			NumberProvider.PACKET_CODEC.decode(buf)
		)
	);

	private final EntityAction entityAction;
	private final EntityAction risingEntityAction;
	private final EntityAction fallingEntityAction;

	private final NumberProvider interval;

	public CallbackTickPower(Properties properties, EntityCondition activeCondition, EntityAction entityAction, EntityAction risingEntityAction, EntityAction fallingEntityAction, NumberProvider interval) {
		super(properties, activeCondition);
		this.entityAction = entityAction;
		this.risingEntityAction = risingEntityAction;
		this.fallingEntityAction = fallingEntityAction;
		this.interval = interval;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.CALLBACK_TICK;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {

		super.validate(reporter);

		getEntityAction().validate(reporter.makeChild(".entity_action"));
		getRisingEntityAction().validate(reporter.makeChild(".rising_action"));
		getFallingEntityAction().validate(reporter.makeChild(".falling_action"));
		getInterval().validate(reporter.makeChild(".interval"));

	}

	public static class Impl extends Power.Impl<CallbackTickPower> {

		private Integer startTicks;
		private Integer endTicks;

		private boolean wasActive;

		protected Impl(@NotNull Entity holder, @NotNull CallbackTickPower power) {
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
							power.getRisingEntityAction().execute(context.makeChild(".rising_action"));
							wasActive = true;
						}

						else {
							power.getEntityAction().execute(context.makeChild(".tick_action"));
						}

					}

				}

				else if (wasActive) {

					if (endTicks == null) {
						this.startTicks = null;
						this.endTicks = ticks;
					}

					else if (ticks == endTicks) {
						power.getFallingEntityAction().execute(context.makeChild(".falling_action"));
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
