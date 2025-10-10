package io.github.eggohito.neo_apoli.provider.type.string;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryAlias;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record StringProviderType<P extends StringProvider>(MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) implements ValueProviderType<P> {

	public static final RegistryAlias<StringProviderType<?>> ALIASES = new RegistryAlias<>(NeoApoliRegistries.STRING_PROVIDER_TYPE);

	public static final Codec<StringProviderType<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);
	public static final PacketCodec<RegistryByteBuf, StringProviderType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.STRING_PROVIDER_TYPE);

}
