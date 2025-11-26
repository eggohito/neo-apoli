package io.github.eggohito.neo_apoli.mixin.power;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.Power;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public abstract class PowerMixin {

	@Mixin(PlayerList.class)
	public static abstract class Callbacks {

		@Inject(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;initInventoryMenu()V"))
		private void onRespawn(ServerPlayer player, boolean alive, Entity.RemovalReason removalReason, CallbackInfoReturnable<ServerPlayer> cir, @Local(ordinal = 1) ServerPlayer newPlayer) {

			if (!alive) {
				PowersComponent.getAllInstances(newPlayer).forEach(Power.Instance::onRespawned);
			}

		}

	}

}
