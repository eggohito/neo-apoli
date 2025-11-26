package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyDamageDealtPower;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public abstract class ModifyDamageDealtPowerMixin {

	@Mixin(LivingEntity.class)
	public static abstract class BaseImpl extends Entity {

		protected BaseImpl(EntityType<?> type, Level world) {
			super(type, world);
		}

		@ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true)
		private float modify(float original, ServerLevel world, DamageSource source, @Share(value = "damageAmountModified", namespace = NeoApoli.MOD_NAMESPACE) LocalBooleanRef damageAmountModifiedRef, @Cancellable CallbackInfoReturnable<Float> cir) {

			if (source.getEntity() != null) {

				Context context = ModifyDamageDealtPower.createContext(source.getEntity(), this, source, original);
				float modified = ModifyDamageDealtPower.modify(context, original);

				damageAmountModifiedRef.set(modified != original);
				return modified;

			}

			else {
				return original;
			}

		}

	}

	@Mixin(Player.class)
	public static abstract class PlayerDelegate extends LivingEntity {

		protected PlayerDelegate(EntityType<? extends LivingEntity> entityType, Level world) {
			super(entityType, world);
		}

		@ModifyReturnValue(method = "hurtServer", at = @At(value = "RETURN", ordinal = 3))
		private boolean delegate(boolean original, ServerLevel world, DamageSource source, float amount, @Share(value = "hasModifyingPowers", namespace = NeoApoli.MOD_NAMESPACE) LocalBooleanRef hasModifyingPowersRef) {

			if (source.getEntity() != null) {
				hasModifyingPowersRef.set(hasModifyingPowersRef.get() || PowersComponent.hasInstances(source.getEntity(), ModifyDamageDealtPower.Instance.class));
			}

			if (hasModifyingPowersRef.get()) {
				return super.hurtServer(world, source, amount);
			}

			else {
				return original;
			}

		}

	}

}
