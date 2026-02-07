package io.github.eggohito.neo_apoli.provider.custom.bool;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface BooleanProvider extends ValueProvider<Boolean> {

	Codec<BooleanProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(BooleanProviderType.CODEC.dispatch(BooleanProvider::getType, BooleanProviderType::mapCodec), ConstantBooleanProvider.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, BooleanProvider> STREAM_CODEC = BooleanProviderType.STREAM_CODEC.dispatch(BooleanProvider::getType, BooleanProviderType::packetCodec);

	@Override
	BooleanProviderType<?> getType();

}
