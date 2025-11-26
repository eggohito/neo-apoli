package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConstantBiEntityCondition(boolean value) implements BiEntityCondition, ConstantMetaCondition {

	public static final Codec<ConstantBiEntityCondition> INLINE_CODEC = ConstantMetaCondition.createInlineCodec(ConstantBiEntityCondition::new);

	public static final MapCodec<ConstantBiEntityCondition> CODEC = ConstantMetaCondition.createCodec(ConstantBiEntityCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantBiEntityCondition> STREAM_CODEC = ConstantMetaCondition.createStreamCodec(ConstantBiEntityCondition::new);

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.CONSTANT;
	}

	@Override
	public String asDisplayString() {
		return BiEntityCondition.super.asDisplayString();
	}

}
