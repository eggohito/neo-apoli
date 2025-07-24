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
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

@Getter
public class CallbackPower extends Power {

	public static final MapCodec<CallbackPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(EntityAction.CODEC.optionalFieldOf("on_added_action", new NothingEntityAction()).forGetter(CallbackPower::getOnAddedAction))
		.and(EntityAction.CODEC.optionalFieldOf("on_granted_action", new NothingEntityAction()).forGetter(CallbackPower::getOnGrantedAction))
		.and(EntityAction.CODEC.optionalFieldOf("on_removed_action", new NothingEntityAction()).forGetter(CallbackPower::getOnRemovedAction))
		.and(EntityAction.CODEC.optionalFieldOf("on_revoked_action", new NothingEntityAction()).forGetter(CallbackPower::getOnRevokedAction))
		.and(EntityAction.CODEC.optionalFieldOf("on_respawned_action", new NothingEntityAction()).forGetter(CallbackPower::getOnRespawnedAction))
		.apply(instance, CallbackPower::new));

	public static final PacketCodec<RegistryByteBuf, CallbackPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, callbackPower) -> {
			EntityAction.PACKET_CODEC.encode(buf, callbackPower.getOnAddedAction());
			EntityAction.PACKET_CODEC.encode(buf, callbackPower.getOnGrantedAction());
			EntityAction.PACKET_CODEC.encode(buf, callbackPower.getOnRemovedAction());
			EntityAction.PACKET_CODEC.encode(buf, callbackPower.getOnRevokedAction());
			EntityAction.PACKET_CODEC.encode(buf, callbackPower.getOnRespawnedAction());
		},
		(buf, properties, condition) -> new CallbackPower(properties, condition,
			EntityAction.PACKET_CODEC.decode(buf),
			EntityAction.PACKET_CODEC.decode(buf),
			EntityAction.PACKET_CODEC.decode(buf),
			EntityAction.PACKET_CODEC.decode(buf),
			EntityAction.PACKET_CODEC.decode(buf)
		)
	);

	private final EntityAction onAddedAction;
	private final EntityAction onGrantedAction;

	private final EntityAction onRemovedAction;
	private final EntityAction onRevokedAction;

	private final EntityAction onRespawnedAction;

	public CallbackPower(Properties properties, EntityCondition activeCondition, EntityAction onAddedAction, EntityAction onGrantedAction, EntityAction onRemovedAction, EntityAction onRevokedAction, EntityAction onRespawnedAction) {
		super(properties, activeCondition);
		this.onAddedAction = onAddedAction;
		this.onGrantedAction = onGrantedAction;
		this.onRemovedAction = onRemovedAction;
		this.onRevokedAction = onRevokedAction;
		this.onRespawnedAction = onRespawnedAction;
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

		getOnAddedAction().validate(reporter.makeChild(".on_added_action"));
		getOnGrantedAction().validate(reporter.makeChild(".on_granted_action"));
		getOnRemovedAction().validate(reporter.makeChild(".on_removed_action"));
		getOnRevokedAction().validate(reporter.makeChild(".on_revoked_action"));
		getOnRespawnedAction().validate(reporter.makeChild(".on_respawned_action"));

	}

	public static class Impl extends Power.Impl<CallbackPower> {

		protected Impl(@NotNull Entity holder, @NotNull CallbackPower power) {
			super(holder, power);
		}

		@Override
		public void onAdded() {

			Context context = this.createGenericContext();

			if (isActive(context)) {
				power.getOnAddedAction().execute(context.makeChild(".on_added_action"));
			}

		}

		@Override
		public void onGranted() {

			Context context = this.createGenericContext();

			if (isActive(context)) {
				power.getOnGrantedAction().execute(context.makeChild(".on_granted_action"));
			}

		}

		@Override
		public void onRemoved() {

			Context context = this.createGenericContext();

			if (isActive(context)) {
				power.getOnRemovedAction().execute(context.makeChild(".on_removed_action"));
			}

		}

		@Override
		public void onRevoked() {

			Context context = this.createGenericContext();

			if (isActive(context)) {
				power.getOnRevokedAction().execute(context.makeChild(".on_revoked_action"));
			}

		}

		@Override
		public void onRespawn() {

			Context context = this.createGenericContext();

			if (isActive(context)) {
				power.getOnRespawnedAction().execute(context.makeChild(".on_respawned_action"));
			}

		}

	}

}
