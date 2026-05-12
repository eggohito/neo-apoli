package io.github.eggohito.neo_apoli.color.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.color.DynamicColor;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliColorTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record RgbaDynamic(NumberProvider red, NumberProvider green, NumberProvider blue, NumberProvider alpha) implements DynamicColor {

	public static final MapCodec<RgbaDynamic> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("red").forGetter(RgbaDynamic::red),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("green").forGetter(RgbaDynamic::green),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("blue").forGetter(RgbaDynamic::blue),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("alpha").forGetter(RgbaDynamic::alpha)
	).apply(instance, RgbaDynamic::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RgbaDynamic> STREAM_CODEC = StreamCodec.composite(
		NumberProvider.STREAM_CODEC, RgbaDynamic::red,
		NumberProvider.STREAM_CODEC, RgbaDynamic::green,
		NumberProvider.STREAM_CODEC, RgbaDynamic::blue,
		NumberProvider.STREAM_CODEC, RgbaDynamic::alpha,
		RgbaDynamic::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliColorTypes.RGBA_DYNAMIC;
	}

	@Override
	public void validate(Context.Validator validator) {
		red().validate(validator.forChild(".red"));
		green().validate(validator.forChild(".green"));
		blue().validate(validator.forChild(".blue"));
		alpha().validate(validator.forChild(".alpha"));
	}

	@Override
	public int intValue(Context context) {
		return new Argb(alpha(context), red(context), green(context), blue(context)).intValue(context);
	}

	public float red(Context context) {
		return DynamicColor.getValue(context.forChild(".red"), red()::nextFloat, () -> 1.0F);
	}

	public float green(Context context) {
		return DynamicColor.getValue(context.forChild(".green"), green()::nextFloat, () -> 1.0F);
	}

	public float blue(Context context) {
		return DynamicColor.getValue(context.forChild(".blue"), blue()::nextFloat, () -> 1.0F);
	}

	public float alpha(Context context) {
		return DynamicColor.getValue(context.forChild(".alpha"), alpha()::nextFloat, () -> 1.0F);
	}

}
