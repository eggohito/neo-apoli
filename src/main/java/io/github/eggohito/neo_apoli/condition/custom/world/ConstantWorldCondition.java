package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionType;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConstantWorldCondition(boolean value) implements WorldCondition, IConstantMetaCondition {

	public static final Codec<ConstantWorldCondition> INLINE_CODEC = IConstantMetaCondition.createInlineCodec(ConstantWorldCondition::new);

	public static final MapCodec<ConstantWorldCondition> MAP_CODEC = IConstantMetaCondition.mapCodec(ConstantWorldCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantWorldCondition> STREAM_CODEC = IConstantMetaCondition.streamCodec(ConstantWorldCondition::new);

	@Override
	public WorldConditionType<?> getType() {
		return WorldConditionTypes.CONSTANT;
	}

}
