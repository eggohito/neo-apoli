package io.github.eggohito.neo_apoli.color.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.color.DynamicColor;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliColorTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record HsvDynamic(NumberProvider hue, NumberProvider saturation, NumberProvider value, NumberProvider alpha) implements DynamicColor {

	public static final MapCodec<HsvDynamic> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.clamped(0.0F, 360.0F).fieldOf("hue").forGetter(HsvDynamic::hue),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("saturation").forGetter(HsvDynamic::saturation),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("value").forGetter(HsvDynamic::value),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("alpha").forGetter(HsvDynamic::alpha)
	).apply(instance, HsvDynamic::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, HsvDynamic> STREAM_CODEC = StreamCodec.composite(
		NumberProvider.STREAM_CODEC, HsvDynamic::hue,
		NumberProvider.STREAM_CODEC, HsvDynamic::saturation,
		NumberProvider.STREAM_CODEC, HsvDynamic::value,
		NumberProvider.STREAM_CODEC, HsvDynamic::alpha,
		HsvDynamic::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliColorTypes.HSV_DYNAMIC;
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
