package io.github.eggohito.neo_apoli.mixin.power;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.power.Power;
import net.minecraft.entity.Entity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public abstract class PowerMixin {

	@Mixin(PlayerManager.class)
	public static abstract class Callbacks {

		@Inject(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;onSpawn()V"))
		private void onRespawn(ServerPlayerEntity player, boolean alive, Entity.RemovalReason removalReason, CallbackInfoReturnable<ServerPlayerEntity> cir, @Local(ordinal = 1) ServerPlayerEntity newPlayer) {

			if (!alive) {
				NeoApoliEntityComponents.POWERS.get(newPlayer).getPowers(true).forEach(Power.Impl::onRespawn);
			}

		}

	}

}
