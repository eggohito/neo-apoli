package io.github.eggohito.neo_apoli.mixin.impl.power.custom.modify_effect_duration;

import io.github.eggohito.neo_apoli.mixin.access.MobEffectInstanceAccessor;
import io.github.eggohito.neo_apoli.power.custom.ModifyEffectDurationPower;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@ModifyVariable(method = {"addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", "forceAddEffect"}, at = @At("HEAD"), argsOnly = true)
	MobEffectInstance modifyDuration(MobEffectInstance original, MobEffectInstance ignored, @Nullable Entity source) {
		return new MobEffectInstance(
			original.getEffect(),
			ModifyEffectDurationPower.modify(this, original, source, original.getDuration()),
			original.getAmplifier(),
			original.isAmbient(),
			original.isVisible(),
			original.showIcon(),
			((MobEffectInstanceAccessor) original).getHiddenEffect()
		);
	}

}
