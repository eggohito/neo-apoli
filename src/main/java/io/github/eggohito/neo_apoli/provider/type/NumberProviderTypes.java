package io.github.eggohito.neo_apoli.provider.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class NumberProviderTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<NumberProvider.Type<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.NUMBER_PROVIDER_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, NumberProvider.Type<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.NUMBER_PROVIDER_TYPE);

	public static final NumberProvider.Type<AttributeNumberProvider> ATTRIBUTE = registerInternal("attribute", AttributeNumberProvider.CODEC, AttributeNumberProvider.PACKET_CODEC);
	public static final NumberProvider.Type<BinomialNumberProvider> BINOMIAL = registerInternal("binomial", BinomialNumberProvider.CODEC, BinomialNumberProvider.PACKET_CODEC);
	public static final NumberProvider.Type<ConstantNumberProvider> CONSTANT = registerInternal("constant", ConstantNumberProvider.CODEC, ConstantNumberProvider.PACKET_CODEC);
	public static final NumberProvider.Type<StorageNumberProvider> STORAGE = registerInternal("storage", StorageNumberProvider.CODEC, StorageNumberProvider.PACKET_CODEC);
	public static final NumberProvider.Type<TimeNumberProvider> TIME = registerInternal("time", TimeNumberProvider.CODEC, TimeNumberProvider.PACKET_CODEC);
	public static final NumberProvider.Type<UniformNumberProvider> UNIFORM = registerInternal("uniform", UniformNumberProvider.CODEC, UniformNumberProvider.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <P extends NumberProvider> NumberProvider.Type<P> registerInternal(String path, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <P extends NumberProvider> NumberProvider.Type<P> register(Identifier id, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.NUMBER_PROVIDER_TYPE, id, new NumberProvider.Type<>(mapCodec, packetCodec));
	}

}
