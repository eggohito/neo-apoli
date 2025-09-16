package io.github.eggohito.neo_apoli.util.color.dynamic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.color.type.ColorType;
import io.github.eggohito.neo_apoli.util.color.type.ColorTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.ColorHelper;

public record DynamicArgb(NumberProvider alpha, NumberProvider red, NumberProvider green, NumberProvider blue) implements DynamicColor {

	public static final MapCodec<DynamicArgb> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("alpha").forGetter(DynamicArgb::alpha),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("red").forGetter(DynamicArgb::red),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("green").forGetter(DynamicArgb::green),
		NumberProvider.clamped(0.0F, 1.0F).fieldOf("blue").forGetter(DynamicArgb::blue)
	).apply(instance, DynamicArgb::new));

	public static final PacketCodec<RegistryByteBuf, DynamicArgb> PACKET_CODEC = PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, DynamicArgb::alpha,
		NumberProvider.PACKET_CODEC, DynamicArgb::red,
		NumberProvider.PACKET_CODEC, DynamicArgb::green,
		NumberProvider.PACKET_CODEC, DynamicArgb::blue,
		DynamicArgb::new
	);

	@Override
	public ColorType<?> getType() {
		return ColorTypes.ARGB_DYNAMIC;
	}

	@Override
	public int getValue(Context context) {
		return ColorHelper.fromFloats(alpha(context), red(context), green(context), blue(context));
	}

	@Override
	public void validate(ErrorReporter reporter) {
		alpha().validate(reporter.makeChild(".alpha"));
		red().validate(reporter.makeChild(".red"));
		green().validate(reporter.makeChild(".green"));
		blue().validate(reporter.makeChild(".blue"));
	}

	public float alpha(Context context) {
		return DynamicColor.getValue(context.makeChild(".alpha"), alpha()::nextFloat, () -> 1.0F);
	}

	public float red(Context context) {
		return DynamicColor.getValue(context.makeChild(".red"), red()::nextFloat, () -> 1.0F);
	}

	public float green(Context context) {
		return DynamicColor.getValue(context.makeChild(".green"), green()::nextFloat, () -> 1.0F);
	}

	public float blue(Context context) {
		return DynamicColor.getValue(context.makeChild(".blue"), blue()::nextFloat, () -> 1.0F);
	}

}
