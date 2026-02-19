package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConstantKeyCondition(boolean value) implements KeyCondition, ConstantMetaCondition {

	public static final Codec<ConstantKeyCondition> INLINE_CODEC = ConstantMetaCondition.createInlineCodec(ConstantKeyCondition::new);

	public static final MapCodec<ConstantKeyCondition> MAP_CODEC = ConstantMetaCondition.mapCodec(ConstantKeyCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantKeyCondition> STREAM_CODEC = ConstantMetaCondition.streamCodec(ConstantKeyCondition::new);

	@Override
	public KeyConditionType<?> getType() {
		return KeyConditionTypes.CONSTANT;
	}

}
