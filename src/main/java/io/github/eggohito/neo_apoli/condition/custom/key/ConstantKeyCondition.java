package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConstantKeyCondition(boolean value) implements KeyCondition, IConstantMetaCondition {

	public static final Codec<ConstantKeyCondition> INLINE_CODEC = IConstantMetaCondition.createInlineCodec(ConstantKeyCondition::new);

	public static final MapCodec<ConstantKeyCondition> MAP_CODEC = IConstantMetaCondition.mapCodec(ConstantKeyCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantKeyCondition> STREAM_CODEC = IConstantMetaCondition.streamCodec(ConstantKeyCondition::new);

	@Override
	public KeyConditionType<?> getType() {
		return KeyConditionTypes.CONSTANT;
	}

}
