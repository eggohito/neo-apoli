package io.github.eggohito.neo_apoli.provider.type.string;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record StringProviderType<P extends StringProvider>(MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> packetCodec) implements ValueProviderType<P> {

	public static final RegistryFixedAlias<StringProviderType<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.STRING_PROVIDER_TYPE);

	public static final Codec<StringProviderType<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);

	public static final StreamCodec<RegistryFriendlyByteBuf, StringProviderType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.STRING_PROVIDER_TYPE);

}
