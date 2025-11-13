package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.function.Function;

public interface InvertedMetaCondition<C extends Condition> extends MetaCondition {

	C condition();

	@Override
	default boolean test(Context context) {
		return !condition().test(context.makeChild(".condition"));
	}

	@Override
	default void validate(ErrorReporter reporter) {
		condition().validate(reporter.makeChild(".condition"));
	}

	static <C extends Condition, M extends InvertedMetaCondition<C>> MapCodec<M> codec(Codec<C> conditionCodec, Function<C, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			conditionCodec.fieldOf("condition").forGetter(InvertedMetaCondition::condition)
		).apply(instance, constructor));
	}

	static <C extends Condition, M extends InvertedMetaCondition<C>> PacketCodec<RegistryByteBuf, M> packetCodec(PacketCodec<RegistryByteBuf, C> conditionCodec, Function<C, M> constructor) {
		return PacketCodec.tuple(
			conditionCodec, InvertedMetaCondition::condition,
			constructor
		);
	}

}
