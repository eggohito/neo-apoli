package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.fabricmc.fabric.api.util.BooleanFunction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface ConstantMetaCondition extends MetaCondition {

	boolean value();

	@Override
	default boolean test(Context context) {
		return value();
	}

	@Override
	default void validate(ProblemReporter reporter) {

	}

	static <M extends ConstantMetaCondition> Codec<M> createInlineCodec(BooleanFunction<M> constructor) {
		return Codec.BOOL.xmap(constructor::apply, ConstantMetaCondition::value);
	}

	static <M extends ConstantMetaCondition> MapCodec<M> createCodec(BooleanFunction<M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.BOOL.fieldOf("value").forGetter(ConstantMetaCondition::value)
		).apply(instance, constructor::apply));
	}

	static <M extends ConstantMetaCondition> StreamCodec<RegistryFriendlyByteBuf, M> createStreamCodec(BooleanFunction<M> constructor) {
		return StreamCodec.composite(
			ByteBufCodecs.BOOL, ConstantMetaCondition::value,
			constructor::apply
		);
	}

}
