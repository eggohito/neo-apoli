package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConditionResultBooleanProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public interface DynamicMetaCondition extends Condition {

	BooleanProvider value();

	@Override
	default boolean test(Context context) {

		Context valueContext = context.forChild(".value");
		boolean value = value().nextBoolean(valueContext);

		return !valueContext.hasErrors()
			&& value;

	}

	@Override
	default void validate(Context.Validator validator) {
		Condition.super.validate(validator);
		value().validate(validator.forChild(".value"));
	}

	static <M extends DynamicMetaCondition> MapCodec<M> mapCodec(Function<BooleanProvider, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance
			.group(BooleanProvider.CODEC.fieldOf("value").forGetter(DynamicMetaCondition::value))
			.apply(instance, constructor));
	}

	static <M extends DynamicMetaCondition> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(Function<BooleanProvider, M> constructor) {
		return StreamCodec.composite(
			BooleanProvider.STREAM_CODEC, ConditionResultBooleanProvider::new,
			constructor
		);
	}

}
