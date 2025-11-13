package io.github.eggohito.neo_apoli.provider.type.string;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.custom.string.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class StringProviderTypes {

	public static final StringProviderType<ChoiceStringProvider> CHOICE = registerInternal("choice", ChoiceStringProvider.CODEC, ChoiceStringProvider.PACKET_CODEC);
	public static final StringProviderType<ConditionalStringProvider> CONDITIONAL = registerInternal("conditional", ConditionalStringProvider.CODEC, ConditionalStringProvider.PACKET_CODEC);
	public static final StringProviderType<ConstantStringProvider> CONSTANT = registerInternal("constant", ConstantStringProvider.CODEC, ConstantStringProvider.PACKET_CODEC);
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
