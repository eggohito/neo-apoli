package io.github.eggohito.neo_apoli.mixin.impl.power.custom.callback_block_place;

import io.github.eggohito.neo_apoli.power.custom.CallbackBlockPlacePower;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

	@Inject(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
	void onBlockPlace(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
		CallbackBlockPlacePower.execute(context);
	}

}
