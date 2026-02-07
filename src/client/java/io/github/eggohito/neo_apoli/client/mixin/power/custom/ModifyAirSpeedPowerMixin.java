package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import io.github.eggohito.neo_apoli.power.custom.ModifyAirSpeedPower;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class ModifyAirSpeedPowerMixin extends AbstractClientPlayer {

	private ModifyAirSpeedPowerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
		super(clientLevel, gameProfile);
	}

	@ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Abilities;getFlyingSpeed()F"))
	private float modify(float original) {
		return ModifyAirSpeedPower.modify(this, original);
	}

}
