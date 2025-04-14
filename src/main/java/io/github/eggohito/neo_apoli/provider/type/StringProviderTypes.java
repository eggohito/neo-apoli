package io.github.eggohito.neo_apoli.provider.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.provider.custom.string.ConstantStringProvider;
import io.github.eggohito.neo_apoli.provider.custom.string.JoinStringProvider;
import io.github.eggohito.neo_apoli.provider.custom.string.NumberStringProvider;
import io.github.eggohito.neo_apoli.provider.custom.string.UuidStringProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class StringProviderTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<StringProvider.Type<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.STRING_PROVIDER_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, StringProvider.Type<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.STRING_PROVIDER_TYPE);

	public static final StringProvider.Type<ConstantStringProvider> CONSTANT = registerInternal("constant", ConstantStringProvider.CODEC, ConstantStringProvider.PACKET_CODEC);
	public static final StringProvider.Type<JoinStringProvider> JOIN = registerInternal("join", JoinStringProvider.CODEC, JoinStringProvider.PACKET_CODEC);
	public static final StringProvider.Type<NumberStringProvider> NUMBER = registerInternal("number", NumberStringProvider.CODEC, NumberStringProvider.PACKET_CODEC);
	public static final StringProvider.Type<UuidStringProvider> UUID = registerInternal("uuid", UuidStringProvider.CODEC, UuidStringProvider.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <P extends StringProvider> StringProvider.Type<P> registerInternal(String path, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <P extends StringProvider> StringProvider.Type<P> register(Identifier id, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.STRING_PROVIDER_TYPE, id, new StringProvider.Type<>(mapCodec, packetCodec));
	}

}
