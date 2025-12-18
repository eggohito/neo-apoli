package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConditionResultBooleanProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public interface DynamicMetaCondition extends MetaCondition {

	BooleanProvider value();

	@Override
	default boolean test(Context context) {

		Context valueContext = context.forChild(".value");
		boolean value = value().next(valueContext);

		return !valueContext.hasErrors()
			&& value;

	}

	@Override
	default void validate(Context.Validator validator) {
		MetaCondition.super.validate(validator);
		value().validate(validator.forChild(".value"));
	}

	static <M extends DynamicMetaCondition> MapCodec<M> createCodec(Function<BooleanProvider, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance
			.group(BooleanProvider.CODEC.fieldOf("value").forGetter(DynamicMetaCondition::value))
			.apply(instance, constructor));
	}

	static <M extends DynamicMetaCondition> StreamCodec<RegistryFriendlyByteBuf, M> createStreamCodec(Function<BooleanProvider, M> constructor) {
		return StreamCodec.composite(
			BooleanProvider.STREAM_CODEC, ConditionResultBooleanProvider::new,
			constructor
		);
	}

}
