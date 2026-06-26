package io.github.eggohito.neo_apoli.mixin.impl.misc.rideable_players;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

	@ModifyExpressionValue(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hasExactlyOnePlayerPassenger()Z"))
	boolean dontRemoveIfRootVehicleIsAPlayer(boolean original, @Local Entity rootVehicle) {
		return original
			&& !(rootVehicle instanceof Player);
	}

}
