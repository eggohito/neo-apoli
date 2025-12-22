package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.fabricmc.fabric.api.util.BooleanFunction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface IConstantMetaCondition extends MetaCondition {

	boolean value();

	@Override
	default boolean test(Context context) {
		return value();
	}

	@Override
	default void validate(Context.Validator validator) {

	}

	static <M extends IConstantMetaCondition> Codec<M> createInlineCodec(BooleanFunction<M> constructor) {
		return Codec.BOOL.xmap(constructor::apply, IConstantMetaCondition::value);
	}

	static <M extends IConstantMetaCondition> MapCodec<M> createCodec(BooleanFunction<M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.BOOL.fieldOf("value").forGetter(IConstantMetaCondition::value)
		).apply(instance, constructor::apply));
	}

	static <M extends IConstantMetaCondition> StreamCodec<RegistryFriendlyByteBuf, M> createStreamCodec(BooleanFunction<M> constructor) {
		return StreamCodec.composite(
			ByteBufCodecs.BOOL, IConstantMetaCondition::value,
			constructor::apply
		);
	}

}
