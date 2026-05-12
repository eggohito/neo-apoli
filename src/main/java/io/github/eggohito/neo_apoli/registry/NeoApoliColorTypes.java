package io.github.eggohito.neo_apoli.registry;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.color.Color;
import io.github.eggohito.neo_apoli.color.custom.*;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class NeoApoliColorTypes {

	public static final Color.Type<Argb> ARGB = registerInternal("argb", Argb.CODEC, Argb.STREAM_CODEC);
	public static final Color.Type<Hsv> HSV = registerInternal("hsv", Hsv.CODEC, Hsv.STREAM_CODEC);
	public static final Color.Type<Rgba> RGBA = registerInternal("rgba", Rgba.CODEC, Rgba.STREAM_CODEC);

	public static final Color.Type<ArgbDynamic> ARGB_DYNAMIC = registerInternal("dynamic/argb", ArgbDynamic.CODEC, ArgbDynamic.STREAM_CODEC);
	public static final Color.Type<HsvDynamic> HSV_DYNAMIC = registerInternal("dynamic/hsv", HsvDynamic.CODEC, HsvDynamic.STREAM_CODEC);
	public static final Color.Type<RgbaDynamic> RGBA_DYNAMIC = registerInternal("dynamic/rgba", RgbaDynamic.CODEC, RgbaDynamic.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends Color> Color.Type<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends Color> Color.Type<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.COLOR_TYPE, id, new Color.Type<>(mapCodec, streamCodec));
	}

}
