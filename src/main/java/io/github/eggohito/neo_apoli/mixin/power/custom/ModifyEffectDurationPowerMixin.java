package io.github.eggohito.neo_apoli.mixin.power.custom;

import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.mixin.access.MobEffectInstanceAccessor;
import io.github.eggohito.neo_apoli.power.custom.ModifyEffectDurationPower;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class ModifyEffectDurationPowerMixin extends Entity {

	private ModifyEffectDurationPowerMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@ModifyVariable(method = {"addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", "forceAddEffect"}, at = @At("HEAD"), argsOnly = true)
	private MobEffectInstance modifyDuration(MobEffectInstance original, MobEffectInstance ignored, @Nullable Entity source) {

		List<ModifyEffectDurationPower.Instance> instances = PowersComponent.getInstances(this, ModifyEffectDurationPower.Instance.class);
		Context context = ModifyEffectDurationPower.createContext(this, source, original);

		return new MobEffectInstance(
			original.getEffect(),
			ModifyEffectDurationPower.modify(context, instances, original.getDuration()),
			original.getAmplifier(),
			original.isAmbient(),
			original.isVisible(),
			original.showIcon(),
			((MobEffectInstanceAccessor) original).getHiddenEffect()
		);

	}

}
