package io.github.eggohito.neo_apoli.mixin.access;

import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemStack.class)
public interface ItemStackAccessor {

	@Invoker("<init>")
	static ItemStack create(ItemLike item, int count, PatchedDataComponentMap components) {
		throw new AssertionError();
	}

}
