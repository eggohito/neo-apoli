package io.github.eggohito.neo_apoli.util.color;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.util.color.type.ColorType;
import io.github.eggohito.neo_apoli.util.color.type.ColorTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

public record Argb(float alpha, float red, float green, float blue) implements Color {

	public static final MapCodec<Argb> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.floatRange(0.0F, 1.0F).fieldOf("alpha").forGetter(Argb::alpha),
		Codec.floatRange(0.0F, 1.0F).fieldOf("red").forGetter(Argb::red),
		Codec.floatRange(0.0F, 1.0F).fieldOf("green").forGetter(Argb::green),
		Codec.floatRange(0.0F, 1.0F).fieldOf("blue").forGetter(Argb::blue)
	).apply(instance, Argb::new));

	public static final PacketCodec<RegistryByteBuf, Argb> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.FLOAT, Argb::alpha,
		PacketCodecs.FLOAT, Argb::red,
		PacketCodecs.FLOAT, Argb::green,
		PacketCodecs.FLOAT, Argb::blue,
		Argb::new
	);

	public static final Argb DEFAULT = new Argb(1.0F, 1.0F, 1.0F, 1.0F);

	public Argb {
		alpha = MathHelper.clamp(alpha, 0.0F, 1.0F);
		red = MathHelper.clamp(red, 0.0F, 1.0F);
		green = MathHelper.clamp(green, 0.0F, 1.0F);
		blue = MathHelper.clamp(blue, 0.0F, 1.0F);
	}

	@Override
	public ColorType<?> type() {
		return ColorTypes.ARGB;
	}

	@Override
	public Argb toArgb() {
		return this;
	}

	//	TODO: Maybe add an argument that determines how the colors are mixed?
	public Argb mix(Argb other) {
		return new Argb(
			alpha() * other.alpha(),
			red() * other.red(),
			green() * other.green(),
			blue() * other.blue()
		);
	}

	public int pack() {
		return ColorHelper.fromFloats(alpha(), red(), green(), blue());
	}

	public static int getAlpha(int argb) {
		return argb >>> 24;
	}

	public static int getRed(int argb) {
		return argb >> 16 & 0xFF;
	}

	public static int getGreen(int argb) {
		return argb >> 8 & 0xFF;
	}

	public static int getBlue(int argb) {
		return argb & 0xFF;
	}

	public static Argb unpack(int argb) {
		return new Argb(
			ColorHelper.floatFromChannel(getAlpha(argb)),
			ColorHelper.floatFromChannel(getRed(argb)),
			ColorHelper.floatFromChannel(getGreen(argb)),
			ColorHelper.floatFromChannel(getBlue(argb))
		);
	}

}
