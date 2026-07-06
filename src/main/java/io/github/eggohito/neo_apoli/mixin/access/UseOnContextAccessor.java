package io.github.eggohito.neo_apoli.mixin.access;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(UseOnContext.class)
public interface UseOnContextAccessor {
	
	@Accessor
	BlockHitResult getHitResult();
	
}
