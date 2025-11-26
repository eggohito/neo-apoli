package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface NbtProvider extends ValueProvider<Tag> {

	Codec<NbtProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(NbtProviderType.CODEC.dispatch(NbtProvider::getType, NbtProviderType::mapCodec), ConstantNbtProvider.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, NbtProvider> STREAM_CODEC = NbtProviderType.STREAM_CODEC.dispatch(NbtProvider::getType, NbtProviderType::packetCodec);

	@Override
	NbtProviderType<?> getType();

	@Override
	default String asDisplayString() {
		return "NBT provider with type \"" + RegistryUtil.getId(NeoApoliRegistries.NBT_PROVIDER_TYPE, this.getType()) + "\"";
	}

}
