package io.github.eggohito.neo_apoli.provider.type.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryAlias;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record NumberProviderType<P extends NumberProvider>(MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) implements ValueProviderType<P> {

	public static final RegistryAlias<NumberProviderType<?>> ALIASES = new RegistryAlias<>(NeoApoliRegistries.NUMBER_PROVIDER_TYPE);

	public static final Codec<NumberProviderType<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);
	public static final PacketCodec<RegistryByteBuf, NumberProviderType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.NUMBER_PROVIDER_TYPE);

}
