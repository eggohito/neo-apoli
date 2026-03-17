package io.github.eggohito.neo_apoli.provider.type.box;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.custom.box.BoxProvider;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record BoxProviderType<P extends BoxProvider>(MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> packetCodec) implements ValueProviderType<P> {

	public static final FixedRegistryAlias<BoxProviderType<?>> ALIASES = FixedRegistryAlias.of(NeoApoliRegistries.BOX_PROVIDER_TYPE);

	public static final Codec<BoxProviderType<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

	public static final StreamCodec<RegistryFriendlyByteBuf, BoxProviderType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.BOX_PROVIDER_TYPE);

}
