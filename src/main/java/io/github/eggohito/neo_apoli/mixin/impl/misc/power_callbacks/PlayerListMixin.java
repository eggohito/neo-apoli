package io.github.eggohito.neo_apoli.mixin.impl.misc.power_callbacks;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.api.power.Powers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

	@Inject(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;initInventoryMenu()V"))
	void onRespawn(ServerPlayer player, boolean keepInventory, Entity.RemovalReason reason, CallbackInfoReturnable<ServerPlayer> cir, @Local(ordinal = 1) ServerPlayer newPlayer) {

		if (!keepInventory) {
			Powers.getAllInstances(newPlayer).forEach(instance -> instance.onRespawned(newPlayer));
		}

	}

}
