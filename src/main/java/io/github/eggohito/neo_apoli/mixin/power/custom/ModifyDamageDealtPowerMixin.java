package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyDamageDealtPower;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

public abstract class ModifyDamageDealtPowerMixin {

	@Mixin(LivingEntity.class)
	public static abstract class BaseImpl extends Entity {

		protected BaseImpl(EntityType<?> type, World world) {
			super(type, world);
		}

		@ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
		private float modify(float original, ServerWorld world, DamageSource source, @Share("modifyDamageDealt$context") LocalRef<Context> modifyDamageDealt$contextRef, @Share(value = "modifyDamageDealt$instances", namespace = NeoApoli.MOD_NAMESPACE) LocalRef<List<ModifyDamageDealtPower.Instance>> modifyDamageDealt$instancesRef, @Share(value = "modifiedDamage", namespace = NeoApoli.MOD_NAMESPACE) LocalBooleanRef modifiedDamageRef, @Cancellable CallbackInfoReturnable<Float> cir) {

			if (source.getAttacker() != null) {

				Context context = ModifyDamageDealtPower.createContext(source.getAttacker(), this, source, original);
				List<ModifyDamageDealtPower.Instance> instances = PowersComponent.getInstances(source.getAttacker(), ModifyDamageDealtPower.Instance.class, instance -> instance.isActive(context));

				float modified = ModifyDamageDealtPower.modify(context, instances, original);

				modifyDamageDealt$contextRef.set(context);
				modifyDamageDealt$instancesRef.set(instances);
				modifiedDamageRef.set(modified != original);

				return modified;

			}

			else {
				return original;
			}

		}

		@ModifyReturnValue(method = "damage", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;becomeAngry(Lnet/minecraft/entity/damage/DamageSource;)V")))
		private boolean invokeActions(boolean original, ServerWorld world, DamageSource source, float amount, @Share("modifyDamageDealt$context") LocalRef<Context> modifyDamageDealt$contextRef, @Share(value = "modifyDamageDealt$instances", namespace = NeoApoli.MOD_NAMESPACE) LocalRef<List<ModifyDamageDealtPower.Instance>> modifyDamageDealt$instancesRef) {

			if (original && modifyDamageDealt$contextRef.get() != null && modifyDamageDealt$instancesRef.get() != null) {
				Context context = modifyDamageDealt$contextRef.get();
				modifyDamageDealt$instancesRef.get().forEach(instance -> instance.execute(context));
			}

			return original;

		}

	}

	@Mixin(PlayerEntity.class)
	public static abstract class PlayerDelegate extends LivingEntity {

		protected PlayerDelegate(EntityType<? extends LivingEntity> entityType, World world) {
			super(entityType, world);
		}

		@ModifyReturnValue(method = "damage", at = @At(value = "RETURN", ordinal = 3))
		private boolean delegate(boolean original, ServerWorld world, DamageSource source, float amount, @Share(value = "hasModifyingPowers", namespace = NeoApoli.MOD_NAMESPACE) LocalBooleanRef hasModifyingPowersRef) {

			if (source.getAttacker() != null) {
				hasModifyingPowersRef.set(hasModifyingPowersRef.get() || PowersComponent.hasInstances(source.getAttacker(), ModifyDamageDealtPower.Instance.class));
			}

			if (hasModifyingPowersRef.get()) {
				return super.damage(world, source, amount);
			}

			else {
				return original;
			}

		}

	}

	@Mixin(ArmorStandEntity.class)
	public static abstract class ArmorStandDelegate extends LivingEntity {

		protected ArmorStandDelegate(EntityType<? extends LivingEntity> entityType, World world) {
			super(entityType, world);
		}

		@Inject(method = "damage", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;isSourceCreativePlayer()Z")))
		private void invokeActions(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {

			if (cir.getReturnValueZ() && source.getAttacker() != null) {

				Entity attacker = source.getAttacker();
				Context context = ModifyDamageDealtPower.createContext(attacker, this, source, amount);

				PowersComponent.getInstances(attacker, ModifyDamageDealtPower.Instance.class, instance -> instance.isActive(context)).forEach(instance -> instance.execute(context));

			}

		}

	}

}
