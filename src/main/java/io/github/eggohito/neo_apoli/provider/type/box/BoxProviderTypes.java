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

	public static final BoxProviderType<ConditionalBoxProvider> CONDITIONAL = registerInternal("conditional", ConditionalBoxProvider.MAP_CODEC, ConditionalBoxProvider.STREAM_CODEC);
	public static final BoxProviderType<ConstantBoxProvider> CONSTANT = registerInternal("constant", ConstantBoxProvider.MAP_CODEC, ConstantBoxProvider.STREAM_CODEC);
	public static final BoxProviderType<OffsetBoxProvider> OFFSET = registerInternal("offset", OffsetBoxProvider.MAP_CODEC, OffsetBoxProvider.STREAM_CODEC);
	public static final BoxProviderType<SwitchBoxProvider> SWITCH = registerInternal("switch", SwitchBoxProvider.MAP_CODEC, SwitchBoxProvider.STREAM_CODEC);
	public static final BoxProviderType<TranslateBoxProvider> TRANSLATE = registerInternal("translate", TranslateBoxProvider.MAP_CODEC, TranslateBoxProvider.STREAM_CODEC);

	public static final BoxProviderType<BlockBoundsBoxProvider> BLOCK_BOUNDS = registerInternal("block/bounds", BlockBoundsBoxProvider.MAP_CODEC, BlockBoundsBoxProvider.STREAM_CODEC);
	public static final BoxProviderType<DynamicBoxProvider> DYNAMIC = registerInternal("dynamic", DynamicBoxProvider.MAP_CODEC, DynamicBoxProvider.STREAM_CODEC);
	public static final BoxProviderType<EntityBoundsBoxProvider> ENTITY_BOUNDS = registerInternal("entity/bounds", EntityBoundsBoxProvider.MAP_CODEC, EntityBoundsBoxProvider.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <P extends BoxProvider> BoxProviderType<P> registerInternal(String path, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <P extends BoxProvider> BoxProviderType<P> register(ResourceLocation id, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> streamCodec) {
		return Registry.register(NeoApoliRegistries.BOX_PROVIDER_TYPE, id, new BoxProviderType<>(mapCodec, streamCodec));
	}

}
