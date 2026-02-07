package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public interface ITestWorldMetaCondition extends MetaCondition {

	WorldCondition condition();

	@Override
	default boolean test(Context context) {
		return condition().test(context.forChild(".condition"));
	}

	@Override
	default void validate(Context.Validator validator) {
		MetaCondition.super.validate(validator);
		condition().validate(validator.forChild(".condition"));
	}

	static <M extends ITestWorldMetaCondition> MapCodec<M> mapCodec(Function<WorldCondition, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance
			.group(WorldCondition.CODEC.fieldOf("condition").forGetter(ITestWorldMetaCondition::condition))
			.apply(instance, constructor));
	}

	static <M extends ITestWorldMetaCondition> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(Function<WorldCondition, M> constructor) {
		return StreamCodec.composite(
			WorldCondition.STREAM_CODEC, ITestWorldMetaCondition::condition,
			constructor
		);
	}

}
