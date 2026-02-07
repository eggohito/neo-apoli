package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.power.custom.ModifyAirSpeedPower;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

public abstract class ModifyAirSpeedPowerMixin {

	@Mixin(LivingEntity.class)
	public static abstract class LivingTarget extends Entity {

		protected LivingTarget(EntityType<?> entityType, Level level) {
			super(entityType, level);
		}

		@ModifyReturnValue(method = "getFlyingSpeed", at = @At("RETURN"))
		private float modify(float original) {

			try {
				return ModifyAirSpeedPower.modify(this, original);
			}

			finally {
				ModifyAirSpeedPower.VISITOR.clear();
			}

		}

	}

	@Mixin(net.minecraft.world.entity.player.Player.class)
	public static abstract class Player extends LivingEntity {

		protected Player(EntityType<? extends LivingEntity> entityType, Level level) {
			super(entityType, level);
		}

		@ModifyReturnValue(method = "getFlyingSpeed", at = @At("RETURN"))
		private float modify(float original) {

			try {
				return ModifyAirSpeedPower.modify(this, original);
			}

			finally {
				ModifyAirSpeedPower.VISITOR.clear();
			}

		}

	}

}
