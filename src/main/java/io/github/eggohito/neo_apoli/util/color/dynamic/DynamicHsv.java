package io.github.eggohito.neo_apoli.util.color.dynamic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.color.Hsv;
import io.github.eggohito.neo_apoli.util.color.type.ColorType;
import io.github.eggohito.neo_apoli.util.color.type.ColorTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record DynamicHsv(NumberProvider hue, NumberProvider saturation, NumberProvider value, NumberProvider alpha) implements DynamicColor {

	public static final MapCodec<DynamicHsv> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.clamped(0.0F, 360.0F).fieldOf("hue").forGetter(DynamicHsv::hue),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("saturation").forGetter(DynamicHsv::saturation),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("value").forGetter(DynamicHsv::value),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("alpha").forGetter(DynamicHsv::alpha)
	).apply(instance, DynamicHsv::new));

	public static final PacketCodec<RegistryByteBuf, DynamicHsv> PACKET_CODEC = PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, DynamicHsv::hue,
		NumberProvider.PACKET_CODEC, DynamicHsv::saturation,
		NumberProvider.PACKET_CODEC, DynamicHsv::value,
		NumberProvider.PACKET_CODEC, DynamicHsv::alpha,
		DynamicHsv::new
	);

	@Override
	public ColorType<?> getType() {
		return ColorTypes.HSV_DYNAMIC;
	}

	@Override
	public int getValue(Context context) {
		return new Hsv(hue(context), saturation(context), value(context), alpha(context)).getValue(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		hue().validate(reporter.makeChild(".hue"));
		saturation().validate(reporter.makeChild(".saturation"));
		value().validate(reporter.makeChild(".value"));
		alpha().validate(reporter.makeChild(".alpha"));
	}

	public float hue(Context context) {
		return DynamicColor.getValue(context.makeChild(".hue"), hue()::nextFloat, () -> 360.0F);
	}

	public float saturation(Context context) {
		return DynamicColor.getValue(context.makeChild(".saturation"), saturation()::nextFloat, () -> 1.0F);
	}

	public float value(Context context) {
		return DynamicColor.getValue(context.makeChild(".value"), value()::nextFloat, () -> 1.0F);
	}

	public float alpha(Context context) {
		return DynamicColor.getValue(context.makeChild(".alpha"), alpha()::nextFloat, () -> 1.0F);
	}

}
