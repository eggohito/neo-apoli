package io.github.eggohito.neo_apoli.provider.type.box;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.custom.box.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class BoxProviderTypes {

	public static final BoxProviderType<ChoiceBoxProvider> CHOICE = registerInternal("choice", ChoiceBoxProvider.CODEC, ChoiceBoxProvider.STREAM_CODEC);
	public static final BoxProviderType<ConditionalBoxProvider> CONDITIONAL = registerInternal("conditional", ConditionalBoxProvider.CODEC, ConditionalBoxProvider.STREAM_CODEC);
	public static final BoxProviderType<ConstantBoxProvider> CONSTANT = registerInternal("constant", ConstantBoxProvider.CODEC, ConstantBoxProvider.STREAM_CODEC);
	public static final BoxProviderType<OffsetBoxProvider> OFFSET = registerInternal("offset", OffsetBoxProvider.CODEC, OffsetBoxProvider.STREAM_CODEC);
	public static final BoxProviderType<TranslateBoxProvider> TRANSLATE = registerInternal("translate", TranslateBoxProvider.CODEC, TranslateBoxProvider.STREAM_CODEC);

	public static final BoxProviderType<BoundingBoxProvider> BOUNDING_BOX = registerInternal("bounding_box", BoundingBoxProvider.CODEC, BoundingBoxProvider.STREAM_CODEC);
	public static final BoxProviderType<DynamicBoxProvider> DYNAMIC = registerInternal("dynamic", DynamicBoxProvider.CODEC, DynamicBoxProvider.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <P extends BoxProvider> BoxProviderType<P> registerInternal(String path, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <P extends BoxProvider> BoxProviderType<P> register(ResourceLocation id, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.BOX_PROVIDER_TYPE, id, new BoxProviderType<>(mapCodec, packetCodec));
	}

}
