package io.github.eggohito.neo_apoli.tag.manager;

import io.github.eggohito.neo_apoli.tag.NestedTag;
import io.github.eggohito.neo_apoli.util.services.Services;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface NestedTagManager {

	NestedTagManager INSTANCE = Services.load(NestedTagManager.class);

	<T> NestedTag<T> getOrCreate(ResourceKey<? extends Registry<T>> registryKey);

	@ApiStatus.Internal
	void init();

}
