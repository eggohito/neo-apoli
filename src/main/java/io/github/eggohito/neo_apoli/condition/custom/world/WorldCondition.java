package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface WorldCondition extends Condition {

	Codec<WorldCondition> CODEC = WorldConditionType.CODEC.dispatch(WorldCondition::getType, WorldConditionType::mapCodec);

	StreamCodec<RegistryFriendlyByteBuf, WorldCondition> STREAM_CODEC = WorldConditionType.STREAM_CODEC.dispatch(WorldCondition::getType, WorldConditionType::streamCodec);

	@Override
	WorldConditionType<?> getType();

	@Override
	default String asDisplayString() {
		return "World condition with type \"" + RegistryUtil.getId(NeoApoliRegistries.WORLD_CONDITION_TYPE, this.getType()) + "\"";
	}

}
