package io.github.eggohito.neo_apoli.color.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.color.DynamicColor;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliColorTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;

public record ArgbDynamic(NumberProvider alpha, NumberProvider red, NumberProvider green, NumberProvider blue) implements DynamicColor {

	public static final MapCodec<ArgbDynamic> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("alpha").forGetter(ArgbDynamic::alpha),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("red").forGetter(ArgbDynamic::red),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("green").forGetter(ArgbDynamic::green),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("blue").forGetter(ArgbDynamic::blue)
	).apply(instance, ArgbDynamic::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ArgbDynamic> STREAM_CODEC = StreamCodec.composite(
		NumberProvider.STREAM_CODEC, ArgbDynamic::alpha,
		NumberProvider.STREAM_CODEC, ArgbDynamic::red,
		NumberProvider.STREAM_CODEC, ArgbDynamic::green,
		NumberProvider.STREAM_CODEC, ArgbDynamic::blue,
		ArgbDynamic::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliColorTypes.ARGB_DYNAMIC;
	}

	@Override
	public int intValue(Context context) {
		return ARGB.colorFromFloat(alpha(context), red(context), green(context), blue(context));
	}

	@Override
	public void validate(Context.Validator validator) {
		alpha().validate(validator.forChild(".alpha"));
		red().validate(validator.forChild(".red"));
		green().validate(validator.forChild(".green"));
		blue().validate(validator.forChild(".blue"));
	}

	public float alpha(Context context) {
		return DynamicColor.getValue(context.forChild(".alpha"), alpha()::nextFloat, () -> 1.0F);
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

}
