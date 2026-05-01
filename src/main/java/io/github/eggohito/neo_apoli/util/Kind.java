package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface Kind<T> {

	ResourceKey<? extends Registry<T>> registryKey();

	Codec<T> codec();

}
