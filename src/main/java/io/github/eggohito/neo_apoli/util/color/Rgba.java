package io.github.eggohito.neo_apoli.util.color;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.util.color.type.ColorType;
import io.github.eggohito.neo_apoli.util.color.type.ColorTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.ColorCode;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

public record Rgba(float red, float green, float blue, float alpha) implements Color {

	public static final MapCodec<Rgba> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.floatRange(0.0F, 1.0F).fieldOf("red").forGetter(Rgba::red),
		Codec.floatRange(0.0F, 1.0F).fieldOf("green").forGetter(Rgba::green),
		Codec.floatRange(0.0F, 1.0F).fieldOf("blue").forGetter(Rgba::blue),
		Codec.floatRange(0.0F, 1.0F).fieldOf("alpha").forGetter(Rgba::alpha)
	).apply(instance, Rgba::new));

	public static final PacketCodec<RegistryByteBuf, Rgba> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.FLOAT, Rgba::red,
		PacketCodecs.FLOAT, Rgba::green,
		PacketCodecs.FLOAT, Rgba::blue,
		PacketCodecs.FLOAT, Rgba::alpha,
		Rgba::new
	);

	public static final Codec<Rgba> STRING_CODEC = ColorCode.CODEC.xmap(
		colorCode ->
			Rgba.unpack(colorCode.rgba()),
		rgba ->
			new ColorCode(rgba.pack())
	);

	public Rgba {
		red = MathHelper.clamp(red, 0.0F, 1.0F);
		green = MathHelper.clamp(green, 0.0F, 1.0F);
		blue = MathHelper.clamp(blue, 0.0F, 1.0F);
		alpha = MathHelper.clamp(alpha, 0.0F, 1.0F);
	}

	@Override
	public ColorType<?> type() {
		return ColorTypes.RGBA;
	}

	@Override
	public Argb toArgb() {
		return new Argb(alpha(), red(), green(), blue());
	}

	public static int getRed(int rgba) {
		return rgba >>> 24;
	}

	public static int getGreen(int rgba) {
		return rgba >> 16 & 0xFF;
	}

	public static int getBlue(int rgba) {
		return rgba >> 8 & 0xFF;
	}

	public static int getAlpha(int rgba) {
		return rgba & 0xFF;
	}

	public int pack() {
		return ColorHelper.channelFromFloat(red()) << 24 | ColorHelper.channelFromFloat(green()) << 16 | ColorHelper.channelFromFloat(blue()) << 8 | ColorHelper.channelFromFloat(alpha());
	}

	public static Rgba unpack(int rgba) {
		return new Rgba(
			ColorHelper.floatFromChannel(getRed(rgba)),
			ColorHelper.floatFromChannel(getGreen(rgba)),
			ColorHelper.floatFromChannel(getBlue(rgba)),
			ColorHelper.floatFromChannel(getAlpha(rgba))
		);
	}

}
