package io.github.eggohito.neo_apoli.color.dynamic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.color.type.ColorType;
import io.github.eggohito.neo_apoli.color.type.ColorTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;

public record DynamicArgb(NumberProvider alpha, NumberProvider red, NumberProvider green, NumberProvider blue) implements DynamicColor {

	public static final MapCodec<DynamicArgb> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("alpha").forGetter(DynamicArgb::alpha),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("red").forGetter(DynamicArgb::red),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("green").forGetter(DynamicArgb::green),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("blue").forGetter(DynamicArgb::blue)
	).apply(instance, DynamicArgb::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicArgb> STREAM_CODEC = StreamCodec.composite(
		NumberProvider.STREAM_CODEC, DynamicArgb::alpha,
		NumberProvider.STREAM_CODEC, DynamicArgb::red,
		NumberProvider.STREAM_CODEC, DynamicArgb::green,
		NumberProvider.STREAM_CODEC, DynamicArgb::blue,
		DynamicArgb::new
	);

	@Override
	public ColorType<?> getType() {
		return ColorTypes.ARGB_DYNAMIC;
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
