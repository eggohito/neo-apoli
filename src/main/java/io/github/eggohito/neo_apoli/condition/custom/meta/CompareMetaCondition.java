package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public interface CompareMetaCondition extends MetaCondition {

	Comparison comparison();

	@Override
	default boolean test(Context context) {
		return comparison().compare(context.forChild(".comparison"));
	}

	@Override
	default void validate(Context.Validator validator) {
		comparison().validate(validator.forChild(".comparison"));
	}

	static <M extends CompareMetaCondition> MapCodec<M> createCodec(Function<Comparison, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Comparison.CODEC.fieldOf("comparison").forGetter(CompareMetaCondition::comparison)
		).apply(instance, constructor));
	}

	static <M extends CompareMetaCondition> StreamCodec<RegistryFriendlyByteBuf, M> createStreamCodec(Function<Comparison, M> constructor) {
		return StreamCodec.composite(
			Comparison.STREAM_CODEC, CompareMetaCondition::comparison,
			constructor
		);
	}

}
