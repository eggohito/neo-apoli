package io.github.eggohito.neo_apoli.mixin.power;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.api.power.Powers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public abstract class PowerMixin {

	@Mixin(PlayerList.class)
	public static abstract class Callbacks {

		@Inject(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;initInventoryMenu()V"))
		private void onRespawn(ServerPlayer player, boolean alive, Entity.RemovalReason removalReason, CallbackInfoReturnable<ServerPlayer> cir, @Local(ordinal = 1) ServerPlayer newPlayer) {

			if (!alive) {
				Powers.getAllInstances(newPlayer).forEach(instance -> instance.onRespawned(newPlayer));
			}

		}

	}

	@Mixin(Entity.class)
	public static abstract class TickCallback {

		@Inject(method = "baseTick", at = @At("TAIL"))
		private void onTick(CallbackInfo ci) {

			var thisAsEntity = (Entity) (Object) this;
			Powers powers = Powers.getNullable(thisAsEntity);

			if (powers == null) {
				return;
			}

			for (var instance : powers.getAllInstances()) {

				if (instance.shouldTick(thisAsEntity)) {
					instance.onTick(thisAsEntity);
				}

			}

		}

	}

}
