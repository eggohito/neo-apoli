package io.github.eggohito.neo_apoli.mixin.power.custom;

import io.github.eggohito.neo_apoli.power.custom.CallbackPlayerWakeUpPower;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class CallbackPlayerWakeUpPowerMixin extends LivingEntity {

	protected CallbackPlayerWakeUpPowerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "stopSleepInBed", at = @At("HEAD"))
	private void executeAction(boolean wakeImmediately, boolean updateLevelForSleepingPlayers, CallbackInfo ci) {

		if (!wakeImmediately && !updateLevelForSleepingPlayers && this.getSleepingPos().isPresent()) {
			CallbackPlayerWakeUpPower.execute((Player) (Object) this, this.getSleepingPos().get());
		}

	}

}
