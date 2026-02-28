package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.power.custom.ModifyBlockBreakSpeedPower;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.class)
public abstract class ModifyBlockBreakSpeedPowerMixin {

	@ModifyReturnValue(method = "getDestroyProgress", at = @At("RETURN"))
	private float modifyBreakSpeed(float original, BlockState blockState, Player player, BlockGetter blockGetter, BlockPos blockPos) {

		try {
			return ModifyBlockBreakSpeedPower.modify(player, blockPos, blockState, blockGetter.getBlockEntity(blockPos), original);
		}

		finally {
			ModifyBlockBreakSpeedPower.VISITOR.clear();
		}

	}

}
