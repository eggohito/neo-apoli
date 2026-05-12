package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliBlockConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AnyOfBlockCondition(List<BlockCondition> conditions) implements BlockCondition, AnyOfMetaCondition<BlockCondition> {

	public static final MapCodec<AnyOfBlockCondition> MAP_CODEC = MapCodecUtil.lazy(AnyOfBlockCondition.class.getSimpleName(), () -> AnyOfMetaCondition.mapCodec(BlockCondition.CODEC, AnyOfBlockCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AnyOfBlockCondition> STREAM_CODEC = StreamCodecUtil.lazy(AnyOfBlockCondition.class.getSimpleName(), () -> AnyOfMetaCondition.streamCodec(BlockCondition.STREAM_CODEC, AnyOfBlockCondition::new));

	@Override
	public BlockCondition.Type<?> getType() {
		return NeoApoliBlockConditionTypes.ANY_OF;
	}

}
