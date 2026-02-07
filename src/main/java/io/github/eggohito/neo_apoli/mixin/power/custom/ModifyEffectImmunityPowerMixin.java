package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.power.custom.ModifyEffectImmunityPower;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class ModifyEffectImmunityPowerMixin extends Entity implements Attackable {

	private ModifyEffectImmunityPowerMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@ModifyExpressionValue(method = {"addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", "forceAddEffect"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;canBeAffected(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
	private boolean immunityOnApply(boolean original, MobEffectInstance effectInstance, @Nullable Entity applier) {

		if (effectInstance.getEffect().value().isInstantenous()) {
			return original;
		}

		else {
			return original
				&& !ModifyEffectImmunityPower.modify(this, effectInstance, applier);
		}

	}

	@ModifyExpressionValue(method = "tickEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;tickServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/lang/Runnable;)Z"))
	private boolean immunityOnTick(boolean original, @Local MobEffectInstance effectInstance) {

		if (effectInstance.getEffect().value().isInstantenous()) {
			return original;
		}

		else {
			return original
				&& !ModifyEffectImmunityPower.modify(this, effectInstance, null);
		}

	}

}
