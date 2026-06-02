package io.github.eggohito.neo_apoli.mixin.impl.power.custom.modify_exhaustion;

import io.github.eggohito.neo_apoli.power.custom.ModifyExhaustionPower;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Player.class)
public abstract class PlayerMixin {

	@ModifyArg(method = "causeFoodExhaustion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"))
	float modifyExhaustion(float exhaustion) {
		return ModifyExhaustionPower.modify((Player) (Object) this, exhaustion);
	}

}
