package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyFallingPower;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;
import java.util.List;
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

	@ModifyReturnValue(method = "getEffectiveGravity", at = @At("RETURN"))
	private double modifyEffectiveGravity(double original, @Local boolean falling) {

		if (!falling) {
			return original;
		}

		Context context = this.neo_apoli$getOrCreateFallingContext();
		List<ModifyFallingPower.Instance> instances = PowersComponent.getInstances(this, ModifyFallingPower.Instance.class);

		double modified = ModifyFallingPower.modify(context, instances, original);

		if (ModifyFallingPower.shouldNegateFallDamage(context, instances)) {
			this.resetFallDistance();
		}

		this.neo_apoli$fallingContext.remove();
		return modified;

	}

}
