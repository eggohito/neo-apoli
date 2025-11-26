package io.github.eggohito.neo_apoli.util.color.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.color.Argb;
import io.github.eggohito.neo_apoli.util.color.Color;
import io.github.eggohito.neo_apoli.util.color.Hsv;
import io.github.eggohito.neo_apoli.util.color.Rgba;
import io.github.eggohito.neo_apoli.util.color.dynamic.DynamicArgb;
import io.github.eggohito.neo_apoli.util.color.dynamic.DynamicHsv;
import io.github.eggohito.neo_apoli.util.color.dynamic.DynamicRgba;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class ColorTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<ColorType<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.COLOR_TYPE, ALIASES);
	public static final StreamCodec<RegistryFriendlyByteBuf, ColorType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.COLOR_TYPE);

	public static final ColorType<Argb> ARGB = registerInternal("argb", Argb.CODEC, Argb.STREAM_CODEC);
	public static final ColorType<Hsv> HSV = registerInternal("hsv", Hsv.CODEC, Hsv.STREAM_CODEC);
	public static final ColorType<Rgba> RGBA = registerInternal("rgba", Rgba.CODEC, Rgba.STREAM_CODEC);

	public static final ColorType<DynamicArgb> ARGB_DYNAMIC = registerInternal("dynamic/argb", DynamicArgb.CODEC, DynamicArgb.STREAM_CODEC);
	public static final ColorType<DynamicHsv> HSV_DYNAMIC = registerInternal("dynamic/hsv", DynamicHsv.CODEC, DynamicHsv.STREAM_CODEC);
	public static final ColorType<DynamicRgba> RGBA_DYNAMIC = registerInternal("dynamic/rgba", DynamicRgba.CODEC, DynamicRgba.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends Color> ColorType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends Color> ColorType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {
		return Registry.register(NeoApoliRegistries.COLOR_TYPE, id, new ColorType<>(mapCodec, packetCodec));
	}

}
