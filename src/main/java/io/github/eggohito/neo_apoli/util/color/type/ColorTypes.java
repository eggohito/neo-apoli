package io.github.eggohito.neo_apoli.util.color.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.color.Argb;
import io.github.eggohito.neo_apoli.util.color.Color;
import io.github.eggohito.neo_apoli.util.color.Hsv;
import io.github.eggohito.neo_apoli.util.color.Rgba;
import io.github.eggohito.neo_apoli.util.color.dynamic.DynamicArgb;
import io.github.eggohito.neo_apoli.util.color.dynamic.DynamicHsv;
import io.github.eggohito.neo_apoli.util.color.dynamic.DynamicRgba;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ColorTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<ColorType<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.COLOR_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, ColorType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.COLOR_TYPE);

	public static final ColorType<Argb> ARGB = registerInternal("argb", Argb.CODEC, Argb.PACKET_CODEC);
	public static final ColorType<Hsv> HSV = registerInternal("hsv", Hsv.CODEC, Hsv.PACKET_CODEC);
	public static final ColorType<Rgba> RGBA = registerInternal("rgba", Rgba.CODEC, Rgba.PACKET_CODEC);

	public static final ColorType<DynamicArgb> ARGB_DYNAMIC = registerInternal("dynamic/argb", DynamicArgb.CODEC, DynamicArgb.PACKET_CODEC);
	public static final ColorType<DynamicHsv> HSV_DYNAMIC = registerInternal("dynamic/hsv", DynamicHsv.CODEC, DynamicHsv.PACKET_CODEC);
	public static final ColorType<DynamicRgba> RGBA_DYNAMIC = registerInternal("dynamic/rgba", DynamicRgba.CODEC, DynamicRgba.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <C extends Color> ColorType<C> registerInternal(String path, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends Color> ColorType<C> register(Identifier id, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return Registry.register(NeoApoliRegistries.COLOR_TYPE, id, new ColorType<>(mapCodec, packetCodec));
	}

}
