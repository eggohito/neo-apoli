package io.github.eggohito.neo_apoli.mixin.impl.power.custom.modify_player_spawn;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.custom.ModifyPlayerSpawnPower;
import io.github.eggohito.neo_apoli.power.custom.misc.PrioritizedPower;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

	@ModifyExpressionValue(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;findRespawnPositionAndUseSpawnBlock(ZLnet/minecraft/world/level/portal/TeleportTransition$PostTeleportTransition;)Lnet/minecraft/world/level/portal/TeleportTransition;"))
	TeleportTransition onRespawn(TeleportTransition original, ServerPlayer player) {

		if (player.getRespawnConfig() != null && !original.missingRespawnBlock()) {
			return original;
		}

		for (var instance : new PrioritizedPower.InstanceCollection<>(player, ModifyPlayerSpawnPower.Instance.class)) {

			try {
				return instance.getOrFindRespawnLocation(player).get(10, TimeUnit.SECONDS);
			}

			catch (ExecutionException | InterruptedException e) {
				NeoApoli.LOGGER.error("Error trying to search for a valid respawn point with {}", instance.id().asDisplayString(false), e);
			}

			catch (TimeoutException e) {
				NeoApoli.LOGGER.warn("{} timed out searching for a valid respawn point!", instance.id().asDisplayString());
			}

		}

		return original;

	}

}
