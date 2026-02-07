package io.github.eggohito.neo_apoli.mixin.power.custom;


import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.mixin.power.misc.DamageModifyingPowerMixin;
import io.github.eggohito.neo_apoli.power.custom.ModifyDamageTakenPower;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public abstract class ModifyDamageTakenPowerMixin {

	@Mixin(LivingEntity.class)
	public static abstract class BaseImpl extends DamageModifyingPowerMixin {

		@ModifyVariable(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"), argsOnly = true)
		private float modify(float original, ServerLevel level, DamageSource source, @Share(value = "modifiedDamageAmount", namespace = NeoApoli.MOD_NAMESPACE) LocalBooleanRef modifiedDamageAmountRef) {

			LivingEntity thisAsLiving = (LivingEntity) (Object) this;
			float modified = ModifyDamageTakenPower.modify(thisAsLiving, source, original);

			modifiedDamageAmountRef.set(modifiedDamageAmountRef.get() || modified != original);
			return modified;

		}

	}

	@Mixin(Player.class)
	public static abstract class PlayerDelegate extends LivingEntity {

		protected PlayerDelegate(EntityType<? extends LivingEntity> entityType, Level level) {
			super(entityType, level);
		}

		@Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;removeEntitiesOnShoulder()V"))
		private void accountForModifyingPowers(ServerLevel level, DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir, @Share(value = "hasDamageModifyingPowers", namespace = NeoApoli.MOD_NAMESPACE) LocalBooleanRef hasDamageModifyingPowersRef) {
			hasDamageModifyingPowersRef.set(hasDamageModifyingPowersRef.get() || PowersComponent.hasInstances(this, ModifyDamageTakenPower.Instance.class));
		}

	}

}
