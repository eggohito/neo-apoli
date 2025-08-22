package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Getter
public class CallbackPowerRemovedPower extends Power {

	public static final MapCodec<CallbackPowerRemovedPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(EntityAction.CODEC.fieldOf("entity_action").forGetter(CallbackPowerRemovedPower::getEntityAction))
		.apply(instance, CallbackPowerRemovedPower::new));

	public static final PacketCodec<RegistryByteBuf, CallbackPowerRemovedPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, power) ->
			EntityAction.PACKET_CODEC.encode(buf, power.getEntityAction()),
		(buf, properties, activeCondition) -> new CallbackPowerRemovedPower(properties, activeCondition,
			EntityAction.PACKET_CODEC.decode(buf)
		)
	);

	private final EntityAction entityAction;

	public CallbackPowerRemovedPower(Properties properties, Optional<EntityCondition> activeCondition, EntityAction entityAction) {
		super(properties, activeCondition);
		this.entityAction = entityAction;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.CALLBACK_POWER_REMOVED;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {
		super.validate(reporter);
		getEntityAction().validate(reporter.makeChild(".entity_action"));
	}

	public static class Instance extends Power.Instance<CallbackPowerRemovedPower> {

		protected Instance(@NotNull Entity holder, @NotNull CallbackPowerRemovedPower power) {
			super(holder, power);
		}

		@Override
		public void onRemoved() {

			super.onRemoved();
			Context context = this.addPowerContext(this.createGenericContext());

			if (this.isActive(context)) {
				power.getEntityAction().execute(context.makeChild(".entity_action"));
			}

		}

	}

}
