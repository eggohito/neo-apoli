package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.fabricmc.fabric.api.util.BooleanFunction;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public interface ConstantMetaCondition extends MetaCondition {

	boolean value();

	@Override
	default boolean test(Context context) {
		return value();
	}

	@Override
	default void validate(ErrorReporter reporter) {

	}

	static <M extends ConstantMetaCondition> Codec<M> inlineCodec(BooleanFunction<M> constructor) {
		return Codec.BOOL.xmap(constructor::apply, ConstantMetaCondition::value);
	}

	static <M extends ConstantMetaCondition> MapCodec<M> codec(BooleanFunction<M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.BOOL.fieldOf("value").forGetter(ConstantMetaCondition::value)
		).apply(instance, constructor::apply));
	}

	static <M extends ConstantMetaCondition> PacketCodec<RegistryByteBuf, M> packetCodec(BooleanFunction<M> constructor) {
		return PacketCodec.tuple(
			PacketCodecs.BOOLEAN, ConstantMetaCondition::value,
			constructor::apply
		);
	}

}
