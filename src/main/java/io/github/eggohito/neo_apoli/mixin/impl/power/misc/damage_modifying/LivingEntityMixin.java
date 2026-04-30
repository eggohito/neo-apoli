package io.github.eggohito.neo_apoli.mixin.impl.power.misc.damage_modifying;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"), cancellable = true)
	void cancelWhenModifiedDamageIsZero(ServerLevel level, DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir, @Share(value = "modifiedDamage", namespace = "neo-apoli:damage_modifying") LocalBooleanRef modifiedDamageRef) {

		if (modifiedDamageRef.get() && Math.signum(amount) <= 0) {
			cir.setReturnValue(false);
		}

	}

}
