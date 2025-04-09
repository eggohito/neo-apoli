package io.github.eggohito.neo_apoli.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import io.github.eggohito.neo_apoli.provider.custom.doubles.ConstantDoubleValueProvider;
import io.github.eggohito.neo_apoli.provider.type.doubles.DoubleValueProviderType;
import io.github.eggohito.neo_apoli.provider.type.doubles.DoubleValueProviderTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface DoubleValueProvider extends ValueProvider {

	String TYPE_KEY = "type";
	MapCodec<DoubleValueProvider> BASE_MAP_CODEC = DoubleValueProviderTypes.CODEC.dispatchMap(TYPE_KEY, DoubleValueProvider::getType, DoubleValueProviderType::mapCodec);

	Codec<DoubleValueProvider> BASE_CODEC = Codec.withAlternative(BASE_MAP_CODEC.codec(), ConstantDoubleValueProvider.INLINE_CODEC);
	PacketCodec<RegistryByteBuf, DoubleValueProvider> BASE_PACKET_CODEC = DoubleValueProviderTypes.PACKET_CODEC.dispatch(DoubleValueProvider::getType, DoubleValueProviderType::packetCodec);

	@Override
	DoubleValueProviderType<?> getType();

	double getDouble(ValueProviderContext context);

}
