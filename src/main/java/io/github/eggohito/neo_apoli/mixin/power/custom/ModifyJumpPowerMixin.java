package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.power.custom.ModifyJumpPower;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class ModifyJumpPowerMixin {

	@ModifyReturnValue(method = "getJumpPower()F", at = @At("RETURN"))
	private float modifyJump(float original) {

		try {
			return ModifyJumpPower.modify(thisAsLiving(), original);
		}

		finally {
			ModifyJumpPower.VISITOR.clear();
		}

	}

	@Unique
	private LivingEntity thisAsLiving() {
		return (LivingEntity) (Object) this;
	}

}
