package io.github.eggohito.neo_apoli.mixin.access;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ResourceKey.class)
public interface ResourceKeyAccessor {

	@Invoker
	static <T> ResourceKey<T> callCreate(ResourceLocation registry, ResourceLocation location) {
		throw new AssertionError();
	}

}
