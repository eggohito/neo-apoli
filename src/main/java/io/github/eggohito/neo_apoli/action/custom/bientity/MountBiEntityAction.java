package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.networking.packet.s2c.MountEntityS2CPacket;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

public record MountBiEntityAction(BooleanProvider force) implements BiEntityAction {

	public static final MapCodec<MountBiEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BooleanProvider.CODEC.optionalFieldOf("force", new ConstantBooleanProvider(false)).forGetter(MountBiEntityAction::force)
	).apply(instance, MountBiEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, MountBiEntityAction> PACKET_CODEC = PacketCodec.tuple(
		BooleanProvider.PACKET_CODEC, MountBiEntityAction::force,
		MountBiEntityAction::new
	);

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.MOUNT;
	}

	@Override
	public void execute(Context context) {

		if (context.getWorld().isClient() || !context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		Entity actor = context.nullable(ContextParameters.ACTOR);
		Entity target = context.nullable(ContextParameters.TARGET);

		if (actor == null || target == null) {
			return;
		}

		Context forceContext = context.makeChild(".force");
		boolean force = force().next(forceContext);

		if (!forceContext.hasErrors() && actor.startRiding(target, force)) {

			MountEntityS2CPacket packet = new MountEntityS2CPacket(actor, target, force);
			Collection<ServerPlayerEntity> trackingPlayers = MiscUtil.getTrackingPlayers(target);

			for (var trackingPlayer : trackingPlayers) {
				ServerPlayNetworking.send(trackingPlayer, packet);
			}

		}

	}

}
