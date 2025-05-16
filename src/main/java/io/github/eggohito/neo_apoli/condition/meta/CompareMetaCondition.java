package io.github.eggohito.neo_apoli.condition.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.function.Function;

public interface CompareMetaCondition<T extends ConditionType<?>> extends Condition<T> {

	@Override
	default boolean test(Context context) {
		return comparison().compare(context);
	}

	@Override
	default void validate(ErrorReporter reporter) {
		Condition.super.validate(reporter);
		comparison().validate(reporter.makeChild("comparison"));
	}

	Comparison comparison();

	static <M extends CompareMetaCondition<?>> MapCodec<M> codec(Function<Comparison, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Comparison.CODEC.fieldOf("comparison").forGetter(CompareMetaCondition::comparison)
		).apply(instance, constructor));
	}

	static <M extends CompareMetaCondition<?>> PacketCodec<RegistryByteBuf, M> packetCodec(Function<Comparison, M> constructor) {
		return PacketCodec.tuple(
			Comparison.PACKET_CODEC, CompareMetaCondition::comparison,
			constructor
		);
	}

}
