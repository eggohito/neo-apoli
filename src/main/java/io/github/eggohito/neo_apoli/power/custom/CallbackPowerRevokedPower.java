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
public class CallbackPowerRevokedPower extends Power {

	public static final MapCodec<CallbackPowerRevokedPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(EntityAction.CODEC.fieldOf("entity_action").forGetter(CallbackPowerRevokedPower::getEntityAction))
		.apply(instance, CallbackPowerRevokedPower::new));

	public static final PacketCodec<RegistryByteBuf, CallbackPowerRevokedPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, power) ->
			EntityAction.PACKET_CODEC.encode(buf, power.getEntityAction()),
		(buf, properties, activeCondition) -> new CallbackPowerRevokedPower(properties, activeCondition,
			EntityAction.PACKET_CODEC.decode(buf)
		)
	);

	private final EntityAction entityAction;

	public CallbackPowerRevokedPower(Properties properties, Optional<EntityCondition> activeCondition, EntityAction entityAction) {
		super(properties, activeCondition);
		this.entityAction = entityAction;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.CALLBACK_POWER_REVOKED;
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

	public static class Instance extends Power.Instance<CallbackPowerRevokedPower> {

		protected Instance(@NotNull Entity holder, @NotNull CallbackPowerRevokedPower power) {
			super(holder, power);
		}

		@Override
		public void onRevoked() {

			super.onRevoked();
			Context context = this.addPowerContext(this.createGenericContext());

			if (this.isActive(context)) {
				power.getEntityAction().execute(context.makeChild(".entity_action"));
			}

		}

	}

}
