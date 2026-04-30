package io.github.eggohito.neo_apoli.mixin.impl.power.custom.modify_air_speed;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntityMixin {

	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@ModifyReturnValue(method = "getFlyingSpeed", at = @At("RETURN"))
	@Override
	protected float modifyAirSpeed(float original) {
		return super.modifyAirSpeed(original);
	}

}
