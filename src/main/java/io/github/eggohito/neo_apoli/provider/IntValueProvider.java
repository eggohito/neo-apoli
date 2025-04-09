package io.github.eggohito.neo_apoli.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import io.github.eggohito.neo_apoli.provider.custom.ints.ConstantIntValueProvider;
import io.github.eggohito.neo_apoli.provider.type.ints.IntValueProviderType;
import io.github.eggohito.neo_apoli.provider.type.ints.IntValueProviderTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface IntValueProvider extends ValueProvider {

	String TYPE_KEY = "type";
	MapCodec<IntValueProvider> BASE_MAP_CODEC = IntValueProviderTypes.CODEC.dispatchMap(TYPE_KEY, IntValueProvider::getType, IntValueProviderType::mapCodec);

	Codec<IntValueProvider> BASE_CODEC = Codec.withAlternative(BASE_MAP_CODEC.codec(), ConstantIntValueProvider.INLINE_CODEC);
	PacketCodec<RegistryByteBuf, IntValueProvider> BASE_PACKET_CODEC = IntValueProviderTypes.PACKET_CODEC.dispatch(IntValueProvider::getType, IntValueProviderType::packetCodec);

	@Override
	IntValueProviderType<?> getType();

	int getInt(ValueProviderContext context);

}
