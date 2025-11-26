package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConstantCondition(boolean value) implements ConstantMetaCondition {

	public static final Codec<ConstantCondition> INLINE_CODEC = ConstantMetaCondition.createInlineCodec(ConstantCondition::new);

	public static final MapCodec<ConstantCondition> CODEC = ConstantMetaCondition.createCodec(ConstantCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantCondition> STREAM_CODEC = ConstantMetaCondition.createStreamCodec(ConstantCondition::new);

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.CONSTANT;
	}

}
