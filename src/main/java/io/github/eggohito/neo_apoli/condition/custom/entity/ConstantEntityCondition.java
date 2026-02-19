package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConstantEntityCondition(boolean value) implements EntityCondition, ConstantMetaCondition {

	public static final Codec<ConstantEntityCondition> INLINE_CODEC = ConstantMetaCondition.createInlineCodec(ConstantEntityCondition::new);

	public static final MapCodec<ConstantEntityCondition> MAP_CODEC = ConstantMetaCondition.mapCodec(ConstantEntityCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantEntityCondition> STREAM_CODEC = ConstantMetaCondition.streamCodec(ConstantEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.CONSTANT;
	}

}
