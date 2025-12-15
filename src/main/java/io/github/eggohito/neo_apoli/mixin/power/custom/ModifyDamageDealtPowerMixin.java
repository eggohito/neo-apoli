package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.mixin.power.misc.DamageModifyingPowerMixin;
import io.github.eggohito.neo_apoli.power.custom.ModifyDamageDealtPower;
import io.github.eggohito.neo_apoli.power.misc.DamageModifyingPower;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;

public abstract class ModifyDamageDealtPowerMixin {

	@Mixin(value = LivingEntity.class, priority = 1001)
	public static abstract class BaseImpl extends DamageModifyingPowerMixin {

		@Override
		protected Context neo_apoli$getOrCreateDamageModifyingContext(DamageSource source, float amount) {

			Context context = Optional.ofNullable(this.neo_apoli$damageModifyingContext.get())
				.flatMap(reference -> Optional.ofNullable(reference.get()))
				.orElseGet(() -> ModifyDamageDealtPower.createContext(source.getEntity(), (LivingEntity) (Object) this, source, amount));

			this.neo_apoli$damageModifyingContext.set(new WeakReference<>(context));
			return context;

		}

		@ModifyVariable(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"), argsOnly = true)
		private float modify(float original, ServerLevel world, DamageSource source, @Share(value = "modifiedDamageAmount", namespace = NeoApoli.MOD_NAMESPACE) LocalBooleanRef modifiedDamageAmountRef) {

			Entity attacker = source.getEntity();
			List<ModifyDamageDealtPower.Instance> instances = PowersComponent.getInstances(attacker, ModifyDamageDealtPower.Instance.class);

			if (attacker == null || instances.isEmpty()) {
				return original;
			}

			Context context = this.neo_apoli$getOrCreateDamageModifyingContext(source, original);
			float modified = DamageModifyingPower.modify(PowerTypes.MODIFY_DAMAGE_DEALT, context, instances, original);

			modifiedDamageAmountRef.set(modifiedDamageAmountRef.get() || modified != original);

			this.neo_apoli$damageModifyingContext.remove();
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
			hasDamageModifyingPowersRef.set(hasDamageModifyingPowersRef.get() || PowersComponent.hasInstances(this, ModifyDamageDealtPower.Instance.class));
		}

	}

}
