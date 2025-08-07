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

@Getter
public class CallbackPlayerRespawnedPower extends Power {

	public static final MapCodec<CallbackPlayerRespawnedPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(EntityAction.CODEC.fieldOf("entity_action").forGetter(CallbackPlayerRespawnedPower::getEntityAction))
		.apply(instance, CallbackPlayerRespawnedPower::new));

	public static final PacketCodec<RegistryByteBuf, CallbackPlayerRespawnedPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, power) ->
			EntityAction.PACKET_CODEC.encode(buf, power.getEntityAction()),
		(buf, properties, activeCondition) -> new CallbackPlayerRespawnedPower(properties, activeCondition,
			EntityAction.PACKET_CODEC.decode(buf)
		)
	);

	private final EntityAction entityAction;

	public CallbackPlayerRespawnedPower(Properties properties, EntityCondition activeCondition, EntityAction entityAction) {
		super(properties, activeCondition);
		this.entityAction = entityAction;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.CALLBACK_PLAYER_RESPAWNED;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {
		super.validate(reporter);
		getEntityAction().validate(reporter.makeChild(".entity_action"));
	}

	public static class Impl extends Power.Impl<CallbackPlayerRespawnedPower> {

		protected Impl(@NotNull Entity holder, @NotNull CallbackPlayerRespawnedPower power) {
			super(holder, power);
		}

		@Override
		public void onRespawned() {

			super.onRespawned();
			Context context = this.addPowerContext(this.createGenericContext());

			if (this.isActive(context)) {
				power.getEntityAction().execute(context.makeChild(".entity_action"));
			}

		}

	}

}
