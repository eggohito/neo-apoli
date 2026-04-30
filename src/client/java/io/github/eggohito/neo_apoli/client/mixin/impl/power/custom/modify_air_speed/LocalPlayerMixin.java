package io.github.eggohito.neo_apoli.client.mixin.impl.power.custom.modify_air_speed;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.eggohito.neo_apoli.mixin.impl.power.custom.modify_air_speed.PlayerMixin;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends PlayerMixin {

	LocalPlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Abilities;getFlyingSpeed()F"))
	@Override
	protected float modifyAirSpeed(float original) {
		return super.modifyAirSpeed(original);
	}

}
