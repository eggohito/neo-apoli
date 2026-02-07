package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.power.custom.ModifyFallingPower;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class ModifyFallingPowerMixin extends Entity {

	private ModifyFallingPowerMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@ModifyReturnValue(method = "getEffectiveGravity", at = @At("RETURN"))
	private double modifyEffectiveGravity(double original, @Local boolean falling) {

		try {

			if (!falling) {
				return original;
			}

			double modified = ModifyFallingPower.modify(this, original);

			if (ModifyFallingPower.shouldNegateFallDamage(this)) {
				this.resetFallDistance();
			}

			return modified;

		}

		finally {
			ModifyFallingPower.VISITOR.clear();
		}

	}

}
