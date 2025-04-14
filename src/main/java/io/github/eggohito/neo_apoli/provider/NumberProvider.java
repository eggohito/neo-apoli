package io.github.eggohito.neo_apoli.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.type.NumberProviderTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface NumberProvider extends ValueProvider<Number> {

	String TYPE_KEY = "type";
	PacketCodec<RegistryByteBuf, NumberProvider> PACKET_CODEC = NumberProviderTypes.PACKET_CODEC.dispatch(NumberProvider::getType, Type::packetCodec);

	MapCodec<NumberProvider> MAP_CODEC = NumberProviderTypes.CODEC.dispatchMap(TYPE_KEY, NumberProvider::getType, Type::mapCodec);
	Codec<NumberProvider> CODEC = Codec.withAlternative(MAP_CODEC.codec(), ConstantNumberProvider.INLINE_CODEC);

	Type<?> getType();

	record Type<P extends NumberProvider>(MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) implements ValueProvider.Type<P> {

	}

}
