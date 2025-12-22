package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConstantBiEntityCondition(boolean value) implements BiEntityCondition, IConstantMetaCondition {

	public static final Codec<ConstantBiEntityCondition> INLINE_CODEC = IConstantMetaCondition.createInlineCodec(ConstantBiEntityCondition::new);

	public static final MapCodec<ConstantBiEntityCondition> CODEC = IConstantMetaCondition.createCodec(ConstantBiEntityCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantBiEntityCondition> STREAM_CODEC = IConstantMetaCondition.createStreamCodec(ConstantBiEntityCondition::new);

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.CONSTANT;
	}

	@Override
	public String asDisplayString() {
		return BiEntityCondition.super.asDisplayString();
	}

}
