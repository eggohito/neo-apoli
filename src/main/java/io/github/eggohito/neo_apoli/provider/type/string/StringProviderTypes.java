package io.github.eggohito.neo_apoli.provider.type.string;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.custom.string.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class StringProviderTypes {

	public static final StringProviderType<ChoiceStringProvider> CHOICE = registerInternal("choice", ChoiceStringProvider.CODEC, ChoiceStringProvider.STREAM_CODEC);
	public static final StringProviderType<ConditionalStringProvider> CONDITIONAL = registerInternal("conditional", ConditionalStringProvider.CODEC, ConditionalStringProvider.STREAM_CODEC);
	public static final StringProviderType<ConstantStringProvider> CONSTANT = registerInternal("constant", ConstantStringProvider.CODEC, ConstantStringProvider.STREAM_CODEC);
	public static final StringProviderType<JoinStringProvider> JOIN = registerInternal("join", JoinStringProvider.CODEC, JoinStringProvider.STREAM_CODEC);
	public static final StringProviderType<NbtStringProvider> NBT = registerInternal("nbt", NbtStringProvider.CODEC, NbtStringProvider.STREAM_CODEC);
	public static final StringProviderType<NumberStringProvider> NUMBER = registerInternal("number", NumberStringProvider.CODEC, NumberStringProvider.STREAM_CODEC);
	public static final StringProviderType<UuidStringProvider> UUID = registerInternal("uuid", UuidStringProvider.CODEC, UuidStringProvider.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <P extends StringProvider> StringProviderType<P> registerInternal(String path, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <P extends StringProvider> StringProviderType<P> register(ResourceLocation id, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.STRING_PROVIDER_TYPE, id, new StringProviderType<>(mapCodec, packetCodec));
	}

}
