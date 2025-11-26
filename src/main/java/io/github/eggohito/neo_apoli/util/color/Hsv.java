package io.github.eggohito.neo_apoli.util.color;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.util.color.type.ColorType;
import io.github.eggohito.neo_apoli.util.color.type.ColorTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public record Hsv(float hue, float saturation, float value, float alpha) implements Color {

	public static final MapCodec<Hsv> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.floatRange(0.0F, 360.0F).fieldOf("hue").forGetter(Hsv::hue),
		Codec.floatRange(0.0F, 1.0F).fieldOf("saturation").forGetter(Hsv::saturation),
		Codec.floatRange(0.0F, 1.0F).fieldOf("value").forGetter(Hsv::value),
		Codec.floatRange(0.0F, 1.0F).fieldOf("alpha").forGetter(Hsv::alpha)
	).apply(instance, Hsv::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, Hsv> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT, Hsv::hue,
		ByteBufCodecs.FLOAT, Hsv::saturation,
		ByteBufCodecs.FLOAT, Hsv::value,
		ByteBufCodecs.FLOAT, Hsv::alpha,
		Hsv::new
	);

	public Hsv {
		hue = Mth.clamp(hue, 0.0F, 360.0F);
		saturation = Mth.clamp(saturation, 0.0F, 1.0F);
		value = Mth.clamp(value, 0.0F, 1.0F);
		alpha = Mth.clamp(alpha, 0.0F, 1.0F);
	}

	@Override
	public ColorType<?> getType() {
		return ColorTypes.HSV;
	}

	@Override
	public int getValue(Context context) {

		int i = (int) (hue() * 6.0F) % 6;
		float j = hue() * 6.0F - i;

		float k = value() * (1.0F - saturation());
		float l = value() * (1.0F - j * saturation());
		float m = value() * (1.0F - (1.0F - j) * saturation());

		float red, green, blue;
		switch (i) {
			case 0 -> {
				red = value();
				green = m;
				blue = k;
			}
			case 1 -> {
				red = l;
				green = value();
				blue = k;
			}
			case 2 -> {
				red = k;
				green = value();
				blue = m;
			}
			case 3 -> {
				red = k;
				green = l;
				blue = value();
			}
			case 4 -> {
				red = m;
				green = k;
				blue = value();
			}
			case 5 -> {
				red = value();
				green = k;
				blue = l;
			}
			default -> {
				context.getReporter().report("Something went wrong when converting HSV to RGB. Input was " + hue() + ", " + saturation() + ", " + value());
				return -1;
			}
		}

		return ARGB.colorFromFloat(alpha(), red, green, blue);

	}

}
