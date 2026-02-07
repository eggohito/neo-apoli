package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public interface ICompareMetaCondition extends MetaCondition {

	Comparison comparison();

	@Override
	default boolean test(Context context) {
		return comparison().compare(context.forChild(".comparison"));
	}

	@Override
	default void validate(Context.Validator validator) {
		comparison().validate(validator.forChild(".comparison"));
	}

	static <M extends ICompareMetaCondition> MapCodec<M> mapCodec(Function<Comparison, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Comparison.CODEC.fieldOf("comparison").forGetter(ICompareMetaCondition::comparison)
		).apply(instance, constructor));
	}

	static <M extends ICompareMetaCondition> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(Function<Comparison, M> constructor) {
		return StreamCodec.composite(
			Comparison.STREAM_CODEC, ICompareMetaCondition::comparison,
			constructor
		);
	}

}
