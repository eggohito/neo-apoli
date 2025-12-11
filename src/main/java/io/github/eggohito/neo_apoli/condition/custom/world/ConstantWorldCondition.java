package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionType;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConstantWorldCondition(boolean value) implements WorldCondition, ConstantMetaCondition {

	public static final Codec<ConstantWorldCondition> INLINE_CODEC = ConstantMetaCondition.createInlineCodec(ConstantWorldCondition::new);

	public static final MapCodec<ConstantWorldCondition> CODEC = ConstantMetaCondition.createCodec(ConstantWorldCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantWorldCondition> STREAM_CODEC = ConstantMetaCondition.createStreamCodec(ConstantWorldCondition::new);

	@Override
	public WorldConditionType<?> getType() {
		return WorldConditionTypes.CONSTANT;
	}

	@Override
	public String asDisplayString() {
		return WorldCondition.super.asDisplayString();
	}

}
