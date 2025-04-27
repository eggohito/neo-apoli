package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.entity.NothingEntityAction;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextType;
import org.jetbrains.annotations.NotNull;

import java.util.function.UnaryOperator;

public class TickingPower extends Power {

	public static final ContextType CONTEXT_TYPE = createContextType(UnaryOperator.identity());

	public static final MapCodec<TickingPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonAndConditionFields(instance)
		.and(EntityAction.CODEC.optionalFieldOf("tick_action", new NothingEntityAction()).forGetter(TickingPower::getTickAction))
		.and(EntityAction.CODEC.optionalFieldOf("first_active_tick_action", new NothingEntityAction()).forGetter(TickingPower::getFirstActiveTickAction))
		.and(EntityAction.CODEC.optionalFieldOf("first_inactive_tick_action", new NothingEntityAction()).forGetter(TickingPower::getFirstInactiveTickAction))
		.and(NumberProvider.ranged(0, Integer.MAX_VALUE).optionalFieldOf("interval", new ConstantNumberProvider(20)).forGetter(TickingPower::getInterval))
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
	public Type<?> getType() {
		return PowerTypes.TICKING;
	}

	@Override
	public Impl createImpl(Entity holder) {
		return new Impl(holder);
	}

	@Override
	public ContextType getContextType() {
		return CONTEXT_TYPE;
	}

	public EntityAction getTickAction() {
		return tickAction;
	}

	public EntityAction getFirstActiveTickAction() {
		return firstActiveTickAction;
	}

	public EntityAction getFirstInactiveTickAction() {
		return firstInactiveTickAction;
	}

	public NumberProvider getInterval() {
		return interval;
	}

	public class Impl extends Power.Impl<TickingPower> {

		private Integer startTicks;
		private Integer endTicks;

		private boolean active;

		protected Impl(@NotNull Entity holder) {
			super(holder, TickingPower.this);
		}

		@Override
		public void onTick() {

			Context context = this.getContextBuilder().build(holder.getWorld());
			int ticks = holder.age % getInterval().get(context.makeChild("interval")).intValue();

			if (isActive()) {

				if (startTicks == null) {
					this.startTicks = ticks;
					this.endTicks = null;
				}

				else if (ticks == startTicks) {

					if (!active) {
						getFirstActiveTickAction().execute(context.makeChild("first_active_tick_action"));
						active = true;
					}

					else {
						getTickAction().execute(context.makeChild("tick_action"));
					}

				}

			}

			else if (active) {

				if (endTicks == null) {
					this.startTicks = null;
					this.endTicks = ticks;
				}

				else if (ticks == endTicks) {
					getFirstInactiveTickAction().execute(context.makeChild("first_inactive_tick_action"));
					active = false;
				}

			}

		}

	}

}
