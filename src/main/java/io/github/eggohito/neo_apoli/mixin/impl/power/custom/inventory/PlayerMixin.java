package io.github.eggohito.neo_apoli.mixin.impl.power.custom.inventory;

import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.power.custom.InventoryPower;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

	PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "dropEquipment", at = @At("TAIL"))
	void dropPowerInventoriesOnDeath(ServerLevel level, CallbackInfo ci) {
		Powers.getInstances(this, InventoryPower.Instance.class).forEach(instance -> instance.dropItemsOnDeath(this));
	}

}
