package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConstantMetaCondition(boolean value) implements IConstantMetaCondition {

	public static final Codec<ConstantMetaCondition> INLINE_CODEC = IConstantMetaCondition.createInlineCodec(ConstantMetaCondition::new);

	public static final MapCodec<ConstantMetaCondition> MAP_CODEC = IConstantMetaCondition.mapCodec(ConstantMetaCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantMetaCondition> STREAM_CODEC = IConstantMetaCondition.streamCodec(ConstantMetaCondition::new);

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.CONSTANT;
	}

}
