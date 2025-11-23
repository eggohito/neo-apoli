package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.power.custom.CallbackDamageDealtPower;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public abstract class CallbackDamageDealtPowerMixin {

	@Mixin(LivingEntity.class)
	public static abstract class BaseImpl extends Entity {

		protected BaseImpl(EntityType<?> type, World world) {
			super(type, world);
		}

		@ModifyReturnValue(method = "damage", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;becomeAngry(Lnet/minecraft/entity/damage/DamageSource;)V")))
		private boolean invokeActions(boolean original, ServerWorld world, DamageSource source, float amount) {

			if (original && source.getAttacker() != null) {

				Context context = CallbackDamageDealtPower.createContext(source.getAttacker(), this, source, amount);

				CallbackDamageDealtPower.execute(context);

			}

			return original;

		}

	}

	@Mixin(ArmorStandEntity.class)
	public static abstract class ArmorStandDelegate extends LivingEntity {

		protected ArmorStandDelegate(EntityType<? extends LivingEntity> entityType, World world) {
			super(entityType, world);
		}

		@Inject(method = "damage", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;isSourceCreativePlayer()Z")))
		private void invokeAction(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {

			if (cir.getReturnValueZ() && source.getAttacker() != null) {

				Context context = CallbackDamageDealtPower.createContext(source.getAttacker(), this, source, amount);

				CallbackDamageDealtPower.execute(context);

			}

		}

	}

}
