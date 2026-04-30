package io.github.eggohito.neo_apoli.color.dynamic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.color.Hsv;
import io.github.eggohito.neo_apoli.color.type.ColorType;
import io.github.eggohito.neo_apoli.color.type.ColorTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicHsv(NumberProvider hue, NumberProvider saturation, NumberProvider value, NumberProvider alpha) implements DynamicColor {

	public static final MapCodec<DynamicHsv> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.clamped(0.0F, 360.0F).fieldOf("hue").forGetter(DynamicHsv::hue),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("saturation").forGetter(DynamicHsv::saturation),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("value").forGetter(DynamicHsv::value),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("alpha").forGetter(DynamicHsv::alpha)
	).apply(instance, DynamicHsv::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicHsv> STREAM_CODEC = StreamCodec.composite(
		NumberProvider.STREAM_CODEC, DynamicHsv::hue,
		NumberProvider.STREAM_CODEC, DynamicHsv::saturation,
		NumberProvider.STREAM_CODEC, DynamicHsv::value,
		NumberProvider.STREAM_CODEC, DynamicHsv::alpha,
		DynamicHsv::new
	);

	@Override
	public ColorType<?> getType() {
		return ColorTypes.HSV_DYNAMIC;
	}

	@Override
	public int intValue(Context context) {
		return new Hsv(hue(context), saturation(context), this.value(context), alpha(context)).intValue(context);
	}

	@Override
	public void validate(Context.Validator validator) {
		hue().validate(validator.forChild(".hue"));
		saturation().validate(validator.forChild(".saturation"));
		value().validate(validator.forChild(".value"));
		alpha().validate(validator.forChild(".alpha"));
	}

	public float hue(Context context) {
		return DynamicColor.getValue(context.forChild(".hue"), hue()::nextFloat, () -> 360.0F);
	}

	public float saturation(Context context) {
		return DynamicColor.getValue(context.forChild(".saturation"), saturation()::nextFloat, () -> 1.0F);
	}

	public float value(Context context) {
		return DynamicColor.getValue(context.forChild(".value"), value()::nextFloat, () -> 1.0F);
	}

	public float alpha(Context context) {
		return DynamicColor.getValue(context.forChild(".alpha"), alpha()::nextFloat, () -> 1.0F);
	}

}
