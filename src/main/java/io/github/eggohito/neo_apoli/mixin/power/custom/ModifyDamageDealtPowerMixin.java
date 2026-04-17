package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.mixin.power.misc.DamageModifyingPowerMixin;
import io.github.eggohito.neo_apoli.power.custom.ModifyDamageDealtPower;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public abstract class ModifyDamageDealtPowerMixin {

	@Mixin(value = LivingEntity.class, priority = 1001)
	public static abstract class BaseImpl extends DamageModifyingPowerMixin {

		@ModifyVariable(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"), argsOnly = true)
		private float modify(float original, ServerLevel world, DamageSource source, @Share(value = "modifiedDamageAmount", namespace = NeoApoli.MOD_NAMESPACE) LocalBooleanRef modifiedDamageAmountRef) {

			if (source.getEntity() == null) {
				return original;
			}

			Entity attacker = source.getEntity();
			float modified = ModifyDamageDealtPower.modify(attacker, (LivingEntity) (Object) this, source, original);

			modifiedDamageAmountRef.set(modifiedDamageAmountRef.get() || modified != original);
			return modified;

		}

	}

	@Mixin(value = Player.class, priority = 1001)
	public static abstract class PlayerDelegate extends LivingEntity {

		protected PlayerDelegate(EntityType<? extends LivingEntity> entityType, Level world) {
			super(entityType, world);
		}

		@Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;removeEntitiesOnShoulder()V"))
		private void accountForModifyingPowers(ServerLevel level, DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir, @Share(value = "hasDamageModifyingPowers", namespace = NeoApoli.MOD_NAMESPACE) LocalBooleanRef hasDamageModifyingPowersRef) {
			hasDamageModifyingPowersRef.set(hasDamageModifyingPowersRef.get() || Powers.hasInstances(this, ModifyDamageDealtPower.Instance.class));
		}

	}

}
