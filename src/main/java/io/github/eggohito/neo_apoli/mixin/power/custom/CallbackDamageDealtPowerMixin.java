package io.github.eggohito.neo_apoli.mixin.power.custom;

import io.github.eggohito.neo_apoli.power.custom.CallbackDamageDealtPower;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public abstract class CallbackDamageDealtPowerMixin {

	@Mixin(LivingEntity.class)
	public static abstract class BaseImpl extends Entity {

		protected BaseImpl(EntityType<?> type, Level world) {
			super(type, world);
		}

		@Inject(method = "hurtServer", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;resolveMobResponsibleForDamage(Lnet/minecraft/world/damagesource/DamageSource;)V")))
		private void invokeActions(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {

			if (cir.getReturnValueZ() && source.getEntity() != null) {
				CallbackDamageDealtPower.execute(source.getEntity(), this, source, amount);
			}

		}

	}

	@Mixin(ArmorStand.class)
	public static abstract class ArmorStandDelegate extends LivingEntity {

		protected ArmorStandDelegate(EntityType<? extends LivingEntity> entityType, Level world) {
			super(entityType, world);
		}

		@Inject(method = "hurtServer", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;isCreativePlayer()Z")))
		private void invokeAction(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {

			if (cir.getReturnValueZ() && source.getEntity() != null) {
				CallbackDamageDealtPower.execute(source.getEntity(), this, source, amount);
			}

		}

	}

}
