package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.context.entity.EntityActionContext;
import io.github.eggohito.neo_apoli.action.meta.entity.NothingEntityAction;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class CallbackPower extends Power {

	public static final MapCodec<CallbackPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonAndConditionFields(instance)
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
	public Type<? extends Power> getType() {
		return PowerTypes.CALLBACK;
	}

	@Override
	public void onAdded(Entity holder) {

		if (isActive(holder)) {
			addedAction.execute(this.getReporter().makeChild("on_added_action"), new EntityActionContext(holder));
		}

	}

	@Override
	public void onGranted(Entity holder) {

		if (isActive(holder)) {
			grantedAction.execute(this.getReporter().makeChild("on_granted_action"), new EntityActionContext(holder));
		}

	}

	@Override
	public void onRemoved(Entity holder) {

		if (isActive(holder)) {
			removedAction.execute(this.getReporter().makeChild("on_removed_action"), new EntityActionContext(holder));
		}

	}

	@Override
	public void onRevoked(Entity holder) {

		if (isActive(holder)) {
			revokedAction.execute(this.getReporter().makeChild("on_revoked_action"), new EntityActionContext(holder));
		}

	}

	@Override
	public void onRespawn(PlayerEntity holder) {

		if (isActive(holder)) {
			respawnAction.execute(this.getReporter().makeChild("on_respawn_action"), new EntityActionContext(holder));
		}

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

}
