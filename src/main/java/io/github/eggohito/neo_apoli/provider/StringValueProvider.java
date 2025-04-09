package io.github.eggohito.neo_apoli.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.strings.ConstantStringValueProvider;
import io.github.eggohito.neo_apoli.provider.type.strings.StringValueProviderType;
import io.github.eggohito.neo_apoli.provider.type.strings.StringValueProviderTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface StringValueProvider extends ObjectValueProvider<String> {

	String TYPE_KEY = "type";
	MapCodec<StringValueProvider> BASE_MAP_CODEC = StringValueProviderTypes.CODEC.dispatchMap(TYPE_KEY, StringValueProvider::getType, StringValueProviderType::mapCodec);

	Codec<StringValueProvider> BASE_CODEC = Codec.withAlternative(BASE_MAP_CODEC.codec(), ConstantStringValueProvider.INLINE_CODEC);
	PacketCodec<RegistryByteBuf, StringValueProvider> BASE_PACKET_CODEC = StringValueProviderTypes.PACKET_CODEC.dispatch(StringValueProvider::getType, StringValueProviderType::packetCodec);

	@Override
	StringValueProviderType<?> getType();

}
