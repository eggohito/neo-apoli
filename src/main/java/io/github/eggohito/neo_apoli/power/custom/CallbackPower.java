package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.entity.NothingEntityAction;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

public class CallbackPower extends Power {

	public static final MapCodec<CallbackPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(EntityAction.CODEC.optionalFieldOf("on_added_action", new NothingEntityAction()).forGetter(CallbackPower::getAddedAction))
		.and(EntityAction.CODEC.optionalFieldOf("on_granted_action", new NothingEntityAction()).forGetter(CallbackPower::getGrantedAction))
		.and(EntityAction.CODEC.optionalFieldOf("on_removed_action", new NothingEntityAction()).forGetter(CallbackPower::getRemovedAction))
		.and(EntityAction.CODEC.optionalFieldOf("on_revoked_action", new NothingEntityAction()).forGetter(CallbackPower::getRevokedAction))
		.and(EntityAction.CODEC.optionalFieldOf("on_respawn_action", new NothingEntityAction()).forGetter(CallbackPower::getRespawnAction))
		.apply(instance, CallbackPower::new));

	public static final PacketCodec<RegistryByteBuf, CallbackPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, callbackPower) -> {
			EntityAction.PACKET_CODEC.encode(buf, callbackPower.getAddedAction());
			EntityAction.PACKET_CODEC.encode(buf, callbackPower.getGrantedAction());
			EntityAction.PACKET_CODEC.encode(buf, callbackPower.getRemovedAction());
			EntityAction.PACKET_CODEC.encode(buf, callbackPower.getRevokedAction());
			EntityAction.PACKET_CODEC.encode(buf, callbackPower.getRespawnAction());
		},
		(buf, properties, condition) -> new CallbackPower(properties, condition,
			EntityAction.PACKET_CODEC.decode(buf),
			EntityAction.PACKET_CODEC.decode(buf),
			EntityAction.PACKET_CODEC.decode(buf),
			EntityAction.PACKET_CODEC.decode(buf),
			EntityAction.PACKET_CODEC.decode(buf)
		)
	);

	private final EntityAction addedAction;
	private final EntityAction grantedAction;

	private final EntityAction removedAction;
	private final EntityAction revokedAction;

	private final EntityAction respawnAction;

	public CallbackPower(Properties properties, EntityCondition activeCondition, EntityAction addedAction, EntityAction grantedAction, EntityAction removedAction, EntityAction revokedAction, EntityAction respawnAction) {
		super(properties, activeCondition);
		this.addedAction = addedAction;
		this.grantedAction = grantedAction;
		this.removedAction = removedAction;
		this.revokedAction = revokedAction;
		this.respawnAction = respawnAction;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.CALLBACK;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {

		super.validate(reporter);

		getAddedAction().validate(reporter.makeChild("on_added_action"));
		getGrantedAction().validate(reporter.makeChild("on_granted_action"));
		getRemovedAction().validate(reporter.makeChild("on_removed_action"));
		getRevokedAction().validate(reporter.makeChild("on_revoked_action"));
		getRespawnAction().validate(reporter.makeChild("on_respawn_action"));

	}

	public EntityAction getAddedAction() {
		return addedAction;
	}

	public EntityAction getGrantedAction() {
		return grantedAction;
	}

	public EntityAction getRemovedAction() {
		return removedAction;
	}

	public EntityAction getRevokedAction() {
		return revokedAction;
	}

	public EntityAction getRespawnAction() {
		return respawnAction;
	}

	public static class Impl extends Power.Impl<CallbackPower> {

		protected Impl(@NotNull Entity holder, @NotNull CallbackPower power) {
			super(holder, power);
		}

		@Override
		public void onAdded() {

			Context context = this.createGenericContext();

			if (isActive(context)) {
				executeAndReport("on_added_action", power.getAddedAction(), context);
			}

		}

		@Override
		public void onGranted() {

			Context context = this.createGenericContext();

			if (isActive(context)) {
				executeAndReport("on_granted_action", power.getGrantedAction(), context);
			}

		}

		@Override
		public void onRemoved() {

			Context context = this.createGenericContext();

			if (isActive(context)) {
				executeAndReport("on_removed_action", power.getRemovedAction(), context);
			}

		}

		@Override
		public void onRevoked() {

			Context context = this.createGenericContext();

			if (isActive(context)) {
				executeAndReport("on_revoked_action", power.getRevokedAction(), context);
			}

		}

		@Override
		public void onRespawn() {

			Context context = this.createGenericContext();

			if (isActive(context)) {
				executeAndReport("on_respawn_action", power.getRespawnAction(), context);
			}

		}

	}

}
