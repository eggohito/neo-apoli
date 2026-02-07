package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IInvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionType;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record InvertedWorldCondition(WorldCondition condition) implements WorldCondition, IInvertedMetaCondition<WorldCondition> {

	public static final MapCodec<InvertedWorldCondition> MAP_CODEC = MapCodecUtil.lazy(InvertedWorldCondition.class.getSimpleName(), () -> IInvertedMetaCondition.mapCodec(WorldCondition.CODEC, InvertedWorldCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, InvertedWorldCondition> STREAM_CODEC = StreamCodecUtil.lazy(InvertedWorldCondition.class.getSimpleName(), () -> IInvertedMetaCondition.streamCodec(WorldCondition.STREAM_CODEC, InvertedWorldCondition::new));

	@Override
	public WorldConditionType<?> getType() {
		return WorldConditionTypes.INVERTED;
	}

}
