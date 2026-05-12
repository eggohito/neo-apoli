package io.github.eggohito.neo_apoli.color.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.color.Color;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliColorTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public record Argb(float alpha, float red, float green, float blue) implements Color {

	public static final MapCodec<Argb> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.floatRange(0.0F, 1.0F).fieldOf("alpha").forGetter(Argb::alpha),
		Codec.floatRange(0.0F, 1.0F).fieldOf("red").forGetter(Argb::red),
		Codec.floatRange(0.0F, 1.0F).fieldOf("green").forGetter(Argb::green),
		Codec.floatRange(0.0F, 1.0F).fieldOf("blue").forGetter(Argb::blue)
	).apply(instance, Argb::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, Argb> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT, Argb::alpha,
		ByteBufCodecs.FLOAT, Argb::red,
		ByteBufCodecs.FLOAT, Argb::green,
		ByteBufCodecs.FLOAT, Argb::blue,
		Argb::new
	);

	public static final Argb DEFAULT = new Argb(1.0F, 1.0F, 1.0F, 1.0F);

	public Argb {
		alpha = Mth.clamp(alpha, 0.0F, 1.0F);
		red = Mth.clamp(red, 0.0F, 1.0F);
		green = Mth.clamp(green, 0.0F, 1.0F);
		blue = Mth.clamp(blue, 0.0F, 1.0F);
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
	public Type<?> getType() {
		return NeoApoliColorTypes.ARGB;
	}

	@Override
	public int intValue(Context context) {
		return ARGB.colorFromFloat(alpha(), red(), green(), blue());
	}

	public static int getAlpha(int argb) {
		return argb >>> 24;
	}

	public static float getAlphaFloat(int argb) {
		return ARGB.from8BitChannel(getAlpha(argb));
	}

	public static int getRed(int argb) {
		return argb >> 16 & 0xFF;
	}

	public static float getRedFloat(int argb) {
		return ARGB.from8BitChannel(getRed(argb));
	}

	public static int getGreen(int argb) {
		return argb >> 8 & 0xFF;
	}

	public static float getGreenFloat(int argb) {
		return ARGB.from8BitChannel(getGreen(argb));
	}

	public static int getBlue(int argb) {
		return argb & 0xFF;
	}

	public static float getBlueFloat(int argb) {
		return ARGB.from8BitChannel(getBlue(argb));
	}

}
