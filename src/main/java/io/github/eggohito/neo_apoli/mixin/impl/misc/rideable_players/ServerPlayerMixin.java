package io.github.eggohito.neo_apoli.mixin.impl.misc.rideable_players;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import io.github.eggohito.neo_apoli.registry.attachment.NeoApoliEntityAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("UnstableApiUsage")
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {

	ServerPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
		super(level, pos, yRot, gameProfile);
	}

	@ModifyExpressionValue(method = "loadAndSpawnParentVehicle", at = @At(value = "INVOKE", target = "Ljava/util/Optional;isEmpty()Z"))
	boolean checkIfPlayerWasRidingAPlayer(boolean original) {
		return this.getAttachedOrElse(NeoApoliEntityAttachments.IS_RIDING_PLAYER, false)
			|| original;
	}

}
