package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.function.Function;

public interface CompareMetaCondition extends MetaCondition {

	Comparison comparison();

	@Override
	default boolean test(Context context) {
		return comparison().compare(context.makeChild(".comparison"));
	}

	@Override
	default void validate(ErrorReporter reporter) {
		comparison().validate(reporter.makeChild(".comparison"));
	}

	static <M extends CompareMetaCondition> MapCodec<M> codec(Function<Comparison, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Comparison.CODEC.fieldOf("comparison").forGetter(CompareMetaCondition::comparison)
		).apply(instance, constructor));
	}

	static <M extends CompareMetaCondition> PacketCodec<RegistryByteBuf, M> packetCodec(Function<Comparison, M> constructor) {
		return PacketCodec.tuple(
			Comparison.PACKET_CODEC, CompareMetaCondition::comparison,
			constructor
		);
	}

}
