package io.github.eggohito.neo_apoli.mixin.impl.power.misc.damage_modifying;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

	PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@ModifyReturnValue(method = "hurtServer", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;removeEntitiesOnShoulder()V")))
	private boolean delegateToSuperWhenModified(boolean original, ServerLevel level, DamageSource source, float amount, @Share(value = "hasDamageModifiers", namespace = "neo-apoli:damage_modifying") LocalBooleanRef hasDamageModifiersRef) {

		if (!original && hasDamageModifiersRef.get()) {
			return super.hurtServer(level, source, amount);
		}

		else {
			return original;
		}

	}

}
