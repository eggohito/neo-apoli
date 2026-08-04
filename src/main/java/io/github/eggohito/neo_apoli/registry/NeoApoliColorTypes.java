package io.github.eggohito.neo_apoli.registry;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.color.Color;
import io.github.eggohito.neo_apoli.color.custom.*;
import io.github.eggohito.neo_apoli.color.custom.dynamic.DynamicArgb;
import io.github.eggohito.neo_apoli.color.custom.dynamic.DynamicHsv;
import io.github.eggohito.neo_apoli.color.custom.dynamic.DynamicRgba;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class NeoApoliColorTypes {

	public static final Color.Type<Argb> ARGB = registerInternal("argb", Argb.CODEC, Argb.STREAM_CODEC);
	public static final Color.Type<Hsv> HSV = registerInternal("hsv", Hsv.CODEC, Hsv.STREAM_CODEC);
	public static final Color.Type<Rgba> RGBA = registerInternal("rgba", Rgba.CODEC, Rgba.STREAM_CODEC);

	public static final Color.Type<DynamicArgb> DYNAMIC_ARGB = registerInternal("dynamic/argb", DynamicArgb.CODEC, DynamicArgb.STREAM_CODEC);
	public static final Color.Type<DynamicHsv> DYNAMIC_HSV = registerInternal("dynamic/hsv", DynamicHsv.CODEC, DynamicHsv.STREAM_CODEC);
	public static final Color.Type<DynamicRgba> DYNAMIC_RGBA = registerInternal("dynamic/rgba", DynamicRgba.CODEC, DynamicRgba.STREAM_CODEC);

	public static final Color.Type<BiomeFoliageColor> BIOME_FOLIAGE = registerInternal("biome/foliage", BiomeFoliageColor.CODEC, BiomeFoliageColor.STREAM_CODEC);
	public static final Color.Type<BiomeGrassColor> BIOME_GRASS = registerInternal("biome/grass", BiomeGrassColor.CODEC, BiomeGrassColor.STREAM_CODEC);
	public static final Color.Type<BiomeWaterColor> BIOME_WATER = registerInternal("biome/water", BiomeWaterColor.CODEC, BiomeWaterColor.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends Color> Color.Type<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends Color> Color.Type<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.COLOR_TYPE, id, new Color.Type<>(mapCodec, streamCodec));
	}

}
