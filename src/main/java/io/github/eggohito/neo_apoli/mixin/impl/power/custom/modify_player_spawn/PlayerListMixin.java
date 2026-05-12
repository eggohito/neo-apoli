package io.github.eggohito.neo_apoli.mixin.impl.power.custom.modify_player_spawn;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.custom.ModifyPlayerSpawnPower;
import io.github.eggohito.neo_apoli.power.custom.misc.PrioritizedPower;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

	@ModifyExpressionValue(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;findRespawnPositionAndUseSpawnBlock(ZLnet/minecraft/world/level/portal/TeleportTransition$PostTeleportTransition;)Lnet/minecraft/world/level/portal/TeleportTransition;"))
	TeleportTransition onRespawn(TeleportTransition original, ServerPlayer player) {

		try {

			if (player.getRespawnConfig() != null && !original.missingRespawnBlock()) {
				return original;
			}

			for (var instance : new PrioritizedPower.InstanceCollection<>(player, ModifyPlayerSpawnPower.Instance.class)) {

				Context context = instance.createHolderContext(player);
				Optional<TeleportTransition> destination = instance.getSpawnTeleport();

				try {

					if (destination.isPresent() && ModifyPlayerSpawnPower.VISITOR.push(instance) && instance.isActive(context)) {
						return destination.get();
					}

				}

				finally {
					ModifyPlayerSpawnPower.VISITOR.pop(instance);
				}

			}

			return original;

		}

		finally {
			ModifyPlayerSpawnPower.VISITOR.clear();
		}

	}

}
