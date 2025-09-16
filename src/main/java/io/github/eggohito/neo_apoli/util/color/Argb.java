package io.github.eggohito.neo_apoli.util.color;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.util.color.type.ColorType;
import io.github.eggohito.neo_apoli.util.color.type.ColorTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
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

	public Argb(int argb) {
		this(
			getAlphaFloat(argb),
			getRedFloat(argb),
			getGreenFloat(argb),
			getBlueFloat(argb)
		);
	}

	@Override
	public ColorType<?> getType() {
		return ColorTypes.ARGB;
	}

	@Override
	public int getValue(Context context) {
		return ColorHelper.fromFloats(alpha(), red(), green(), blue());
	}

	public static int getAlpha(int argb) {
		return argb >>> 24;
	}

	public static float getAlphaFloat(int argb) {
		return ColorHelper.floatFromChannel(getAlpha(argb));
	}

	public static int getRed(int argb) {
		return argb >> 16 & 0xFF;
	}

	public static float getRedFloat(int argb) {
		return ColorHelper.floatFromChannel(getRed(argb));
	}

	public static int getGreen(int argb) {
		return argb >> 8 & 0xFF;
	}

	public static float getGreenFloat(int argb) {
		return ColorHelper.floatFromChannel(getGreen(argb));
	}

	public static int getBlue(int argb) {
		return argb & 0xFF;
	}

	public static float getBlueFloat(int argb) {
		return ColorHelper.floatFromChannel(getBlue(argb));
	}

}
