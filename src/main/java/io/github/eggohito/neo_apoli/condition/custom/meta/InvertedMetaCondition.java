package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public interface InvertedMetaCondition<C extends Condition> extends MetaCondition {

	C condition();

	@Override
	default boolean test(Context context) {
		return !condition().test(context.makeChild(".condition"));
	}

	@Override
	default void validate(ProblemReporter reporter) {
		condition().validate(reporter.forChild(".condition"));
	}

	static <C extends Condition, M extends InvertedMetaCondition<C>> MapCodec<M> createCodec(Codec<C> conditionCodec, Function<C, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			conditionCodec.fieldOf("condition").forGetter(InvertedMetaCondition::condition)
		).apply(instance, constructor));
	}

	static <C extends Condition, M extends InvertedMetaCondition<C>> StreamCodec<RegistryFriendlyByteBuf, M> createStreamCodec(StreamCodec<RegistryFriendlyByteBuf, C> conditionCodec, Function<C, M> constructor) {
		return StreamCodec.composite(
			conditionCodec, InvertedMetaCondition::condition,
			constructor
		);
	}

}
