package io.github.eggohito.neo_apoli.mixin.power.custom;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyDamageTakenPower;
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

public abstract class ModifyDamageTakenPowerMixin {

	@Mixin(LivingEntity.class)
	public static abstract class BaseImpl extends Entity {

		private BaseImpl(EntityType<?> entityType, Level level) {
			super(entityType, level);
		}

		@ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true)
		private float modify(float original, ServerLevel level, DamageSource source, @Share(value = "damageAmountModified", namespace = NeoApoli.MOD_NAMESPACE) LocalBooleanRef damageAmountModifiedRef) {

			Context context = ModifyDamageTakenPower.createContext(source.getEntity(), this, source, original);
			float modified = ModifyDamageTakenPower.modify(context, original);

			damageAmountModifiedRef.set(damageAmountModifiedRef.get() || modified != original);
			return modified;

		}

	}

	@Mixin(Player.class)
	public static abstract class PlayerDelegate extends LivingEntity {

		protected PlayerDelegate(EntityType<? extends LivingEntity> entityType, Level world) {
			super(entityType, world);
		}

		@ModifyReturnValue(method = "hurtServer", at = @At(value = "RETURN", ordinal = 3))
		private boolean delegate(boolean original, ServerLevel world, DamageSource source, float amount, @Share(value = "hasModifyingPowers", namespace = NeoApoli.MOD_NAMESPACE) LocalBooleanRef hasModifyingPowersRef) {

			hasModifyingPowersRef.set(hasModifyingPowersRef.get() || PowersComponent.hasInstances(this, ModifyDamageTakenPower.Instance.class));

			if (hasModifyingPowersRef.get()) {
				return super.hurtServer(world, source, amount);
			}

			else {
				return original;
			}

		}

	}

}
