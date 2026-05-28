package io.github.eggohito.neo_apoli.color.custom.dynamic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.color.DynamicColor;
import io.github.eggohito.neo_apoli.color.custom.Argb;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliColorTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicRgba(NumberProvider red, NumberProvider green, NumberProvider blue, NumberProvider alpha) implements DynamicColor {

	public static final MapCodec<DynamicRgba> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("red").forGetter(DynamicRgba::red),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("green").forGetter(DynamicRgba::green),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("blue").forGetter(DynamicRgba::blue),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("alpha").forGetter(DynamicRgba::alpha)
	).apply(instance, DynamicRgba::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicRgba> STREAM_CODEC = StreamCodec.composite(
		NumberProvider.STREAM_CODEC, DynamicRgba::red,
		NumberProvider.STREAM_CODEC, DynamicRgba::green,
		NumberProvider.STREAM_CODEC, DynamicRgba::blue,
		NumberProvider.STREAM_CODEC, DynamicRgba::alpha,
		DynamicRgba::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliColorTypes.DYNAMIC_RGBA;
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
		return DynamicColor.getValue(context.forChild(".red"), red()::getFloat, () -> 1.0F);
	}

	public float green(Context context) {
		return DynamicColor.getValue(context.forChild(".green"), green()::getFloat, () -> 1.0F);
	}

	public float blue(Context context) {
		return DynamicColor.getValue(context.forChild(".blue"), blue()::getFloat, () -> 1.0F);
	}

	public float alpha(Context context) {
		return DynamicColor.getValue(context.forChild(".alpha"), alpha()::getFloat, () -> 1.0F);
	}

}
