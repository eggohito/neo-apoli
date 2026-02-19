package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public interface InvertedMetaCondition<C extends Condition> extends Condition {

	C condition();

	@Override
	default boolean test(Context context) {
		return !condition().test(context.forChild(".condition"));
	}

	@Override
	default void validate(Context.Validator validator) {
		condition().validate(validator.forChild(".condition"));
	}

	static <C extends Condition, M extends InvertedMetaCondition<C>> MapCodec<M> mapCodec(Codec<C> conditionCodec, Function<C, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			conditionCodec.fieldOf("condition").forGetter(InvertedMetaCondition::condition)
		).apply(instance, constructor));
	}

	static <C extends Condition, M extends InvertedMetaCondition<C>> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(StreamCodec<RegistryFriendlyByteBuf, C> conditionCodec, Function<C, M> constructor) {
		return StreamCodec.composite(
			conditionCodec, InvertedMetaCondition::condition,
			constructor
		);
	}

}
