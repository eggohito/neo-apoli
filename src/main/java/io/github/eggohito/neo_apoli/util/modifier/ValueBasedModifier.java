package io.github.eggohito.neo_apoli.util.modifier;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public interface ValueBasedModifier extends Modifier {

	@Override
	ModifierType<?> getType();

	@Override
	default double apply(Context context, double base, double total) {
		return calculate(value().nextDouble(context.makeChild(".value")), base, total);
	}

	@Override
	default void validate(ErrorReporter reporter) {
		Modifier.super.validate(reporter);
		value().validate(reporter.makeChild(".value"));
	}

	NumberProvider value();

	double calculate(double value, double base, double total);

	static <M extends ValueBasedModifier> MapCodec<M> createValueBasedCodec(Function3<Phase, Integer, NumberProvider, M> constructor, int defaultOrder) {
		return RecordCodecBuilder.mapCodec(instance -> Modifier
			.addPhaseAndOrderFields(instance, defaultOrder)
			.and(NumberProvider.CODEC.fieldOf("value").forGetter(ValueBasedModifier::value))
			.apply(instance, constructor));
	}

	static <M extends ValueBasedModifier> PacketCodec<RegistryByteBuf, M> createValueBasedPacketCodec(Function3<Phase, Integer, NumberProvider, M> constructor) {
		return PacketCodec.tuple(
			Phase.PACKET_CODEC, ValueBasedModifier::phase,
			PacketCodecs.INTEGER, ValueBasedModifier::order,
			NumberProvider.PACKET_CODEC, ValueBasedModifier::value,
			constructor
		);
	}

}
