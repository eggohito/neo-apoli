package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionType;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record InvertedWorldCondition(WorldCondition condition) implements WorldCondition, InvertedMetaCondition<WorldCondition> {

	public static final MapCodec<InvertedWorldCondition> MAP_CODEC = MapCodecUtil.lazy(InvertedWorldCondition.class.getSimpleName(), () -> InvertedMetaCondition.mapCodec(WorldCondition.CODEC, InvertedWorldCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, InvertedWorldCondition> STREAM_CODEC = StreamCodecUtil.lazy(InvertedWorldCondition.class.getSimpleName(), () -> InvertedMetaCondition.streamCodec(WorldCondition.STREAM_CODEC, InvertedWorldCondition::new));

	@Override
	public WorldConditionType<?> getType() {
		return WorldConditionTypes.INVERTED;
	}

}
