package io.github.eggohito.neo_apoli.provider.type.nbt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.nbt.NbtProvider;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record NbtProviderType<P extends NbtProvider>(MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> packetCodec) implements ValueProviderType<P> {

	public static final RegistryFixedAlias<NbtProviderType<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.NBT_PROVIDER_TYPE);

	public static final Codec<NbtProviderType<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);

	public static final StreamCodec<RegistryFriendlyByteBuf, NbtProviderType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.NBT_PROVIDER_TYPE);

}
