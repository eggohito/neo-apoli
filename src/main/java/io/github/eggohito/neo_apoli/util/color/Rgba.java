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

	public static final Codec<Rgba> STRING_CODEC = ColorCode.CODEC.xmap(Rgba::new, Rgba::toColorCode);

	public Rgba {
		red = MathHelper.clamp(red, 0.0F, 1.0F);
		green = MathHelper.clamp(green, 0.0F, 1.0F);
		blue = MathHelper.clamp(blue, 0.0F, 1.0F);
		alpha = MathHelper.clamp(alpha, 0.0F, 1.0F);
	}

	public Rgba(ColorCode colorCode) {
		this(colorCode.rgba());
	}

	public Rgba(int rgba) {
		this(
			getRedFloat(rgba),
			getGreenFloat(rgba),
			getBlueFloat(rgba),
			getAlphaFloat(rgba)
		);
	}

	@Override
	public ColorType<?> getType() {
		return ColorTypes.RGBA;
	}

	@Override
	public int getValue(Context context) {
		return this.internalGet(); //	No need to use the context in this case, since this class doesn't use any number providers
	}

	private ColorCode toColorCode() {
		return new ColorCode(this.internalGet());
	}

	private int internalGet() {
		return ColorHelper.channelFromFloat(red()) << 24
			| ColorHelper.channelFromFloat(green()) << 16
			| ColorHelper.channelFromFloat(blue()) << 8
			| ColorHelper.channelFromFloat(alpha());
	}

	public static int getRed(int rgba) {
		return rgba >>> 24;
	}

	public static float getRedFloat(int rgba) {
		return ColorHelper.channelFromFloat(getRed(rgba));
	}

	public static int getGreen(int rgba) {
		return rgba >> 16 & 0xFF;
	}

	public static float getGreenFloat(int rgba) {
		return ColorHelper.channelFromFloat(getGreen(rgba));
	}

	public static int getBlue(int rgba) {
		return rgba >> 8 & 0xFF;
	}

	public static float getBlueFloat(int rgba) {
		return ColorHelper.channelFromFloat(getBlue(rgba));
	}

	public static int getAlpha(int rgba) {
		return rgba & 0xFF;
	}

	public static float getAlphaFloat(int rgba) {
		return ColorHelper.channelFromFloat(getAlpha(rgba));
	}

}
