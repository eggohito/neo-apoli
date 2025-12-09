package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyEffectImmunityPower;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class ModifyEffectImmunityPowerMixin extends Entity implements Attackable {

	private ModifyEffectImmunityPowerMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Unique
	private final ThreadLocal<WeakReference<Context>> neo_apoli$effectImmunityContext = new ThreadLocal<>();

	@Unique
	private Context neo_apoli$getOrCreateEffectImmunityContext(@Nullable Entity source, MobEffectInstance effectInstance) {

		Context context = Optional.ofNullable(this.neo_apoli$effectImmunityContext.get())
			.flatMap(reference -> Optional.ofNullable(reference.get()))
			.orElseGet(() -> ModifyEffectImmunityPower.createContext(this, source, effectInstance));

		this.neo_apoli$effectImmunityContext.set(new WeakReference<>(context));
		return context;

	}

	@ModifyExpressionValue(method = {"addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", "forceAddEffect"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;canBeAffected(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
	private boolean immunityOnApply(boolean original, MobEffectInstance effectInstance, @Nullable Entity applier) {

		if (!original || effectInstance.getEffect().value().isInstantenous()) {
			return original;
		}

		List<ModifyEffectImmunityPower.Instance> instances = PowersComponent.getInstances(this, ModifyEffectImmunityPower.Instance.class);

		if (instances.isEmpty()) {
			return true;
		}

		Context context = this.neo_apoli$getOrCreateEffectImmunityContext(applier, effectInstance);
		boolean result = !ModifyEffectImmunityPower.modify(context, instances);

		this.neo_apoli$effectImmunityContext.remove();
		return result;

	}

	@ModifyExpressionValue(method = "tickEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;tickServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/lang/Runnable;)Z"))
	private boolean immunityOnTick(boolean original, @Local MobEffectInstance effectInstance) {

		if (!original || effectInstance.getEffect().value().isInstantenous()) {
			return original;
		}

		List<ModifyEffectImmunityPower.Instance> instances = PowersComponent.getInstances(this, ModifyEffectImmunityPower.Instance.class);

		if (instances.isEmpty()) {
			return true;
		}

		Context context = this.neo_apoli$getOrCreateEffectImmunityContext(null, effectInstance);
		boolean result = !ModifyEffectImmunityPower.modify(context, instances);

		this.neo_apoli$effectImmunityContext.remove();
		return result;

	}

}
