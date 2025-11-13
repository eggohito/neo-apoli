package io.github.eggohito.neo_apoli.provider.type.box;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.custom.box.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BoxProviderTypes {

	public static final BoxProviderType<ChoiceBoxProvider> CHOICE = registerInternal("choice", ChoiceBoxProvider.CODEC, ChoiceBoxProvider.PACKET_CODEC);
	public static final BoxProviderType<ConditionalBoxProvider> CONDITIONAL = registerInternal("conditional", ConditionalBoxProvider.CODEC, ConditionalBoxProvider.PACKET_CODEC);
	public static final BoxProviderType<ConstantBoxProvider> CONSTANT = registerInternal("constant", ConstantBoxProvider.CODEC, ConstantBoxProvider.PACKET_CODEC);
	public static final BoxProviderType<OffsetBoxProvider> OFFSET = registerInternal("offset", OffsetBoxProvider.CODEC, OffsetBoxProvider.PACKET_CODEC);
	public static final BoxProviderType<TranslateBoxProvider> TRANSLATE = registerInternal("translate", TranslateBoxProvider.CODEC, TranslateBoxProvider.PACKET_CODEC);

	public static final BoxProviderType<BoundingBoxProvider> BOUNDING_BOX = registerInternal("bounding_box", BoundingBoxProvider.CODEC, BoundingBoxProvider.PACKET_CODEC);
	public static final BoxProviderType<DynamicBoxProvider> DYNAMIC = registerInternal("dynamic", DynamicBoxProvider.CODEC, DynamicBoxProvider.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <P extends BoxProvider> BoxProviderType<P> registerInternal(String path, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <P extends BoxProvider> BoxProviderType<P> register(Identifier id, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.BOX_PROVIDER_TYPE, id, new BoxProviderType<>(mapCodec, packetCodec));
	}

}
