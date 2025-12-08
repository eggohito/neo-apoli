package io.github.eggohito.neo_apoli.mixin.power.misc;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import java.lang.ref.WeakReference;

@Mixin(Entity.class)
public abstract class DamageModifyingPowerMixin {

	@Unique
	protected final ThreadLocal<WeakReference<Context>> neo_apoli$damageModifyingContext = new ThreadLocal<>();

	@Unique
	protected abstract Context neo_apoli$getOrCreateDamageModifyingContext(DamageSource source, float amount);

	@Mixin(Player.class)
	public static abstract class PlayerDelegate extends LivingEntity {

		protected PlayerDelegate(EntityType<? extends LivingEntity> entityType, Level level) {
			super(entityType, level);
		}

		@ModifyReturnValue(method = "hurtServer", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;removeEntitiesOnShoulder()V")))
		private boolean delegateToSuper(boolean original, ServerLevel level, DamageSource source, float amount, @Share(value = "hasDamageModifyingPowers", namespace = NeoApoli.MOD_NAMESPACE) LocalBooleanRef hasDamageModifyingPowersRef) {

			if (hasDamageModifyingPowersRef.get()) {
				return super.hurtServer(level, source, amount);
			}

			else {
				return original;
			}

		}

	}

}
