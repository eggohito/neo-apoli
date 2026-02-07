package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface WorldCondition extends Condition {

	Codec<WorldCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(WorldConditionType.CODEC.dispatch(WorldCondition::getType, WorldConditionType::mapCodec), ConstantWorldCondition.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, WorldCondition> STREAM_CODEC = WorldConditionType.STREAM_CODEC.dispatch(WorldCondition::getType, WorldConditionType::streamCodec);

	@Override
	WorldConditionType<?> getType();

}
