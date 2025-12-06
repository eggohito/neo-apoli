package io.github.eggohito.neo_apoli.mixin.power.custom;

import io.github.eggohito.neo_apoli.power.custom.ModifyFallingPower;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.lang.ref.WeakReference;
import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class ModifyFallingPowerMixin extends Entity {

	private ModifyFallingPowerMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Unique
	protected final ThreadLocal<WeakReference<Context>> neo_apoli$fallingContext = new ThreadLocal<>();

	@Unique
	protected Context neo_apoli$getOrCreateFallingContext() {

		Context context = Optional.ofNullable(this.neo_apoli$fallingContext.get())
			.flatMap(reference -> Optional.ofNullable(reference.get()))
			.orElseGet(() -> ModifyFallingPower.createContext(this));

		this.neo_apoli$fallingContext.set(new WeakReference<>(context));
		return context;

	}

	@ModifyVariable(method = "travelInAir", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getEffect(Lnet/minecraft/core/Holder;)Lnet/minecraft/world/effect/MobEffectInstance;"))
	private double impl(double original, Vec3 travelVector) {

		if (Math.signum(travelVector.y) >= 1.0) {
			return original;
		}

		Context context = this.neo_apoli$getOrCreateFallingContext();
		double modified = ModifyFallingPower.modify(context, original);

		if (ModifyFallingPower.shouldNegateFallDamage(context)) {
			this.fallDistance = 0;
		}

		this.neo_apoli$fallingContext.remove();
		return modified;

	}

}
