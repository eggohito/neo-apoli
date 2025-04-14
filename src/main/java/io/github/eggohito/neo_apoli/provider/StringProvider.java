package io.github.eggohito.neo_apoli.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.string.ConstantStringProvider;
import io.github.eggohito.neo_apoli.provider.type.StringProviderTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface StringProvider extends ValueProvider<String> {

	String TYPE_KEY = "type";
	PacketCodec<RegistryByteBuf, StringProvider> PACKET_CODEC = StringProviderTypes.PACKET_CODEC.dispatch(StringProvider::getType, Type::packetCodec);

	MapCodec<StringProvider> MAP_CODEC = StringProviderTypes.CODEC.dispatchMap(TYPE_KEY, StringProvider::getType, Type::mapCodec);
	Codec<StringProvider> CODEC = Codec.withAlternative(MAP_CODEC.codec(), ConstantStringProvider.INLINE_CODEC);

	@Override
	Type<?> getType();

	record Type<P extends StringProvider>(MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) implements ValueProvider.Type<P> {

	}

}
