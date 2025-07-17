package io.github.eggohito.neo_apoli.util.color.dynamic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.FloatFunction;
import io.github.eggohito.neo_apoli.util.FloatSupplier;
import io.github.eggohito.neo_apoli.util.color.Argb;
import io.github.eggohito.neo_apoli.util.color.Color;
import io.github.eggohito.neo_apoli.util.color.Hsv;
import io.github.eggohito.neo_apoli.util.color.type.ColorType;
import io.github.eggohito.neo_apoli.util.color.type.ColorTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record DynamicHsv(NumberProvider hueProvider, NumberProvider saturationProvider, NumberProvider valueProvider, NumberProvider alphaProvider) implements Color {

	public static final MapCodec<DynamicHsv> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.clamped(0.0F, 360.0F).fieldOf("hue").forGetter(DynamicHsv::hueProvider),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("saturation").forGetter(DynamicHsv::saturationProvider),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("value").forGetter(DynamicHsv::valueProvider),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("alpha").forGetter(DynamicHsv::alphaProvider)
	).apply(instance, DynamicHsv::new));

	public static final PacketCodec<RegistryByteBuf, DynamicHsv> PACKET_CODEC = PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, DynamicHsv::hueProvider,
		NumberProvider.PACKET_CODEC, DynamicHsv::saturationProvider,
		NumberProvider.PACKET_CODEC, DynamicHsv::valueProvider,
		NumberProvider.PACKET_CODEC, DynamicHsv::alphaProvider,
		DynamicHsv::new
	);

	@Override
	public ColorType<?> type() {
		return ColorTypes.HSV_DYNAMIC;
	}

	@Override
	public Argb toArgb(Context context) {
		return new Hsv(hue(context), saturation(context), value(context), alpha(context)).toArgb();
	}

	@Override
	public Argb toArgb() {
		throw new IllegalArgumentException("Missing required context for converting dynamic HSV to Argb!");
	}

	public float hue(Context context) {
		return getValue(context.makeChild(".hue"), hueProvider()::nextFloat, () -> 360.0F);
	}

	public float saturation(Context context) {
		return getValue(context.makeChild(".saturation"), saturationProvider()::nextFloat, () -> 1.0F);
	}

	public float value(Context context) {
		return getValue(context.makeChild(".value"), valueProvider()::nextFloat, () -> 1.0F);
	}

	public float alpha(Context context) {
		return getValue(context.makeChild(".alpha"), alphaProvider()::nextFloat, () -> 1.0F);
	}

	private float getValue(Context context, FloatFunction<Context> getter, FloatSupplier defaultValue) {
		float value = getter.apply(context);
		return context.hasErrors() ? defaultValue.getAsFloat() : value;
	}

}
