package io.github.eggohito.neo_apoli.provider.type;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface ValueProviderType<P extends ValueProvider> {

	MapCodec<P> mapCodec();

	StreamCodec<RegistryFriendlyByteBuf, P> streamCodec();

}
