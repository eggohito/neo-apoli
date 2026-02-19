package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public interface NbtProvider extends ValueProvider {

	Codec<NbtProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(NbtProviderType.CODEC.dispatch(NbtProvider::getType, NbtProviderType::mapCodec), ConstantNbtProvider.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, NbtProvider> STREAM_CODEC = NbtProviderType.STREAM_CODEC.dispatch(NbtProvider::getType, NbtProviderType::packetCodec);

	@NotNull
	NbtProviderType<?> getType();

	@NotNull
	Tag nextTag(Context context);

}
