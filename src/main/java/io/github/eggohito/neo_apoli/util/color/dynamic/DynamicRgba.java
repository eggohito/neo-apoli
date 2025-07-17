package io.github.eggohito.neo_apoli.util.color.dynamic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.FloatFunction;
import io.github.eggohito.neo_apoli.util.FloatSupplier;
import io.github.eggohito.neo_apoli.util.color.Argb;
import io.github.eggohito.neo_apoli.util.color.Color;
import io.github.eggohito.neo_apoli.util.color.type.ColorType;
import io.github.eggohito.neo_apoli.util.color.type.ColorTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record DynamicRgba(NumberProvider redProvider, NumberProvider greenProvider, NumberProvider blueProvider, NumberProvider alphaProvider) implements Color {

	public static final MapCodec<DynamicRgba> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("red").forGetter(DynamicRgba::redProvider),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("red").forGetter(DynamicRgba::greenProvider),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("red").forGetter(DynamicRgba::blueProvider),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("red").forGetter(DynamicRgba::alphaProvider)
	).apply(instance, DynamicRgba::new));

	public static final PacketCodec<RegistryByteBuf, DynamicRgba> PACKET_CODEC = PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, DynamicRgba::redProvider,
		NumberProvider.PACKET_CODEC, DynamicRgba::greenProvider,
		NumberProvider.PACKET_CODEC, DynamicRgba::blueProvider,
		NumberProvider.PACKET_CODEC, DynamicRgba::alphaProvider,
		DynamicRgba::new
	);

	@Override
	public ColorType<?> type() {
		return ColorTypes.RGBA_DYNAMIC;
	}

	@Override
	public Argb toArgb(Context context) {
		return new Argb(alpha(context), red(context), green(context), blue(context));
	}

	@Override
	public Argb toArgb() {
		throw new IllegalArgumentException("Missing required context for converting dynamic RGBA to Argb!");
	}

	public float red(Context context) {
		return this.getValue(context.makeChild(".red"), redProvider()::nextFloat, () -> 1.0F);
	}

	public float green(Context context) {
		return this.getValue(context.makeChild(".green"), greenProvider()::nextFloat, () -> 1.0F);
	}

	public float blue(Context context) {
		return this.getValue(context.makeChild(".blue"), blueProvider()::nextFloat, () -> 1.0F);
	}

	public float alpha(Context context) {
		return this.getValue(context.makeChild(".alpha"), alphaProvider()::nextFloat, () -> 1.0F);
	}

	private float getValue(Context context, FloatFunction<Context> getter, FloatSupplier defaultValue) {
		float value = getter.apply(context);
		return context.hasErrors() ? defaultValue.getAsFloat() : value;
	}

}
