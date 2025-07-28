package io.github.eggohito.neo_apoli.provider.type.string;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.provider.custom.string.UuidStringProvider;
import io.github.eggohito.neo_apoli.provider.meta.string.*;
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

	public static final Codec<StringProviderType<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.STRING_PROVIDER_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, StringProviderType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.STRING_PROVIDER_TYPE);

	public static final StringProviderType<ConstantStringProvider> CONSTANT = registerInternal("constant", ConstantStringProvider.CODEC, ConstantStringProvider.PACKET_CODEC);
	public static final StringProviderType<IfElseListStringProvider> IF_ELSE_LIST = registerInternal("if_else_list", IfElseListStringProvider.CODEC, IfElseListStringProvider.PACKET_CODEC);
	public static final StringProviderType<IfElseStringProvider> IF_ELSE = registerInternal("if_else", IfElseStringProvider.CODEC, IfElseStringProvider.PACKET_CODEC);
	public static final StringProviderType<JoinStringProvider> JOIN = registerInternal("join", JoinStringProvider.CODEC, JoinStringProvider.PACKET_CODEC);
	public static final StringProviderType<NbtStringProvider> NBT = registerInternal("nbt", NbtStringProvider.CODEC, NbtStringProvider.PACKET_CODEC);
	public static final StringProviderType<NumberStringProvider> NUMBER = registerInternal("number", NumberStringProvider.CODEC, NumberStringProvider.PACKET_CODEC);

	public static final StringProviderType<UuidStringProvider> UUID = registerInternal("uuid", UuidStringProvider.CODEC, UuidStringProvider.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <P extends StringProvider> StringProviderType<P> registerInternal(String path, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <P extends StringProvider> StringProviderType<P> register(Identifier id, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.STRING_PROVIDER_TYPE, id, new StringProviderType<>(mapCodec, packetCodec));
	}

}
