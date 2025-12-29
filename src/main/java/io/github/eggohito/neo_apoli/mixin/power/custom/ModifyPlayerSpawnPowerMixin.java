package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.eggohito.neo_apoli.power.custom.ModifyPlayerSpawnPower;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

public abstract class ModifyPlayerSpawnPowerMixin {

	@Mixin(PlayerList.class)
	public static abstract class ModifyRespawnPoint {

		@ModifyExpressionValue(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;findRespawnPositionAndUseSpawnBlock(ZLnet/minecraft/world/level/portal/TeleportTransition$PostTeleportTransition;)Lnet/minecraft/world/level/portal/TeleportTransition;"))
		private TeleportTransition modify(TeleportTransition original, ServerPlayer player) {

			if (player.getRespawnConfig() != null && !original.missingRespawnBlock()) {
				return original;
			}

			for (var instance : new Prioritized.InstanceCollection<>(player, ModifyPlayerSpawnPower.Instance.class)) {

				Context context = instance.createHolderContext();
				Optional<TeleportTransition> modified = instance.getRespawnTeleport();

				try {

					if (modified.isPresent() && context.markActive(instance) && instance.isActive(context)) {
						return modified.get();
					}

				}

				finally {
					context.markInActive(instance);
				}

			}

			return original;

		}

	}

}
