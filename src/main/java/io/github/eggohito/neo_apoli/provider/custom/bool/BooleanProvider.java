package io.github.eggohito.neo_apoli.provider.custom.bool;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface BooleanProvider extends ValueProvider<Boolean> {

	Codec<BooleanProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(BooleanProviderType.CODEC.dispatch(BooleanProvider::getType, BooleanProviderType::mapCodec), ConstantBooleanProvider.INLINE_CODEC));

	PacketCodec<RegistryByteBuf, BooleanProvider> PACKET_CODEC = BooleanProviderType.PACKET_CODEC.dispatch(BooleanProvider::getType, BooleanProviderType::packetCodec);

	@Override
	BooleanProviderType<?> getType();

	@Override
	default String asDisplayString() {
		return "Boolean provider with type \"" + RegistryUtil.getId(NeoApoliRegistries.BOOLEAN_PROVIDER_TYPE, this.getType()) + "\"";
	}

}
