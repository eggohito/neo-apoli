package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface NbtProvider extends ValueProvider<NbtElement> {

	Codec<NbtProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(NbtProviderType.CODEC.dispatch(NbtProvider::getType, NbtProviderType::mapCodec), ConstantNbtProvider.INLINE_CODEC));

	PacketCodec<RegistryByteBuf, NbtProvider> PACKET_CODEC = NbtProviderType.PACKET_CODEC.dispatch(NbtProvider::getType, NbtProviderType::packetCodec);

	@Override
	NbtProviderType<?> getType();

	@Override
	default String asDisplayString() {
		return "NBT provider with type \"" + RegistryUtil.getId(NeoApoliRegistries.NBT_PROVIDER_TYPE, this.getType()) + "\"";
	}

}
