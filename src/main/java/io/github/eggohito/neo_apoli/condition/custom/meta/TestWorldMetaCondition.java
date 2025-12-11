package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public interface TestWorldMetaCondition extends MetaCondition {

	WorldCondition condition();

	@Override
	default boolean test(Context context) {
		return condition().test(context.makeChild(".condition"));
	}

	@Override
	default void validate(ProblemReporter reporter) {
		MetaCondition.super.validate(reporter);
		condition().validate(reporter.forChild(".condition"));
	}

	static <M extends TestWorldMetaCondition> MapCodec<M> createCodec(Function<WorldCondition, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance
			.group(WorldCondition.CODEC.fieldOf("condition").forGetter(TestWorldMetaCondition::condition))
			.apply(instance, constructor));
	}

	static <M extends TestWorldMetaCondition> StreamCodec<RegistryFriendlyByteBuf, M> createStreamCodec(Function<WorldCondition, M> constructor) {
		return StreamCodec.composite(
			WorldCondition.STREAM_CODEC, TestWorldMetaCondition::condition,
			constructor
		);
	}

}
