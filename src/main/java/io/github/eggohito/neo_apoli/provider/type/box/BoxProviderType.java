package io.github.eggohito.neo_apoli.provider.type.box;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.BoxProvider;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryAlias;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record BoxProviderType<B extends BoxProvider>(MapCodec<B> mapCodec, PacketCodec<RegistryByteBuf, B> packetCodec) implements ValueProviderType<B> {

	public static final RegistryAlias<BoxProviderType<?>> ALIASES = new RegistryAlias<>(NeoApoliRegistries.BOX_PROVIDER_TYPE);

	public static final Codec<BoxProviderType<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);
	public static final PacketCodec<RegistryByteBuf, BoxProviderType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.BOX_PROVIDER_TYPE);

}
