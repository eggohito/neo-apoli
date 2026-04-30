package io.github.eggohito.neo_apoli.mixin.impl.power.custom.modify_damage_dealt;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.power.custom.ModifyDamageDealtPower;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Player.class, priority = 999)
public abstract class PlayerMixin extends LivingEntity {

	PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;removeEntitiesOnShoulder()V"))
	void accountForModifyingPowers(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir, @Share(value = "hasDamageModifiers", namespace = "neo-apoli:damage_modifying") LocalBooleanRef hasDamageModifiersRef) {
		hasDamageModifiersRef.set(hasDamageModifiersRef.get() || Powers.hasInstances(source.getEntity(), ModifyDamageDealtPower.Instance.class));
	}

}
