package io.github.eggohito.neo_apoli.mixin.impl.power.custom.modify_damage_taken;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import io.github.eggohito.neo_apoli.power.custom.ModifyDamageTakenPower;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = LivingEntity.class, priority = 998)
public abstract class LivingEntityMixin extends Entity {

	LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@ModifyVariable(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"), argsOnly = true)
	float modify(float original, ServerLevel serverLevel, DamageSource source, @Share(value = "modifiedDamage", namespace = "neo-apoli:damage_modifying") LocalBooleanRef modifiedDamageRef) {

		float modified = ModifyDamageTakenPower.modify(this, source, original);
		modifiedDamageRef.set(modifiedDamageRef.get() || modified != original);

		return modified;

	}

}
