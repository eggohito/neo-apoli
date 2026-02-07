package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConstantBlockCondition(boolean value) implements BlockCondition, IConstantMetaCondition {

	public static final Codec<ConstantBlockCondition> INLINE_CODEC = IConstantMetaCondition.createInlineCodec(ConstantBlockCondition::new);

	public static final MapCodec<ConstantBlockCondition> MAP_CODEC = IConstantMetaCondition.mapCodec(ConstantBlockCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantBlockCondition> STREAM_CODEC = IConstantMetaCondition.streamCodec(ConstantBlockCondition::new);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.CONSTANT;
	}

}
