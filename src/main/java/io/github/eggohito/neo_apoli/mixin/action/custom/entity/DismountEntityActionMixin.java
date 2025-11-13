package io.github.eggohito.neo_apoli.mixin.action.custom.entity;

import io.github.eggohito.neo_apoli.networking.packet.s2c.DismountEntityS2CPacket;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(Entity.class)
public abstract class DismountEntityActionMixin {

	@Shadow
	public abstract @Nullable Entity getVehicle();

	@Shadow
	public abstract int getId();

	@Inject(method = "dismountVehicle", at = @At("HEAD"))
	private void syncDismount(CallbackInfo ci) {

		if (this.getVehicle() instanceof ServerPlayerEntity) {

			DismountEntityS2CPacket packet = new DismountEntityS2CPacket(this.getId());
			Set<ServerPlayerEntity> trackingPlayers = MiscUtil.getTrackingPlayers((Entity) (Object) this);

			for (var trackingPlayer: trackingPlayers) {
				ServerPlayNetworking.send(trackingPlayer, packet);
			}

		}

	}

}
