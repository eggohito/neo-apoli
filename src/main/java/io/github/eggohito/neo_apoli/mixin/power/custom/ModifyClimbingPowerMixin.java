package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.ModifyClimbingPower;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class ModifyClimbingPowerMixin extends Entity {

	protected ModifyClimbingPowerMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@ModifyExpressionValue(method = "onClimbable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z"))
	private boolean modifyClimbing(boolean original) {

		try {
			return original
				|| ModifyClimbingPower.modify(this, Power.Instance::isActive);
		}

		finally {
			ModifyClimbingPower.VISITOR.clear();
		}

	}

	@ModifyReturnValue(method = "isSuppressingSlidingDownLadder", at = @At("RETURN"))
	private boolean overrideClimbingHold(boolean original) {

		try {
			return PowersComponent.hasInstances(this, ModifyClimbingPower.Instance.class)
				? ModifyClimbingPower.modify(this, ModifyClimbingPower.Instance::canHold)
				: original;
		}

		finally {
			ModifyClimbingPower.VISITOR.clear();
		}

	}

}
