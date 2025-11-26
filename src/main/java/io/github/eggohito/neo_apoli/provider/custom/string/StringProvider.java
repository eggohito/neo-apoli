package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface StringProvider extends ValueProvider<String> {

	Codec<StringProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(StringProviderType.CODEC.dispatch(StringProvider::getType, StringProviderType::mapCodec), ConstantStringProvider.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, StringProvider> STREAM_CODEC = StringProviderType.STREAM_CODEC.dispatch(StringProvider::getType, StringProviderType::packetCodec);

	@Override
	StringProviderType<?> getType();

	@Override
	default String asDisplayString() {
		return "String provider with type \"" + RegistryUtil.getId(NeoApoliRegistries.STRING_PROVIDER_TYPE, this.getType()) + "\"";
	}

}
