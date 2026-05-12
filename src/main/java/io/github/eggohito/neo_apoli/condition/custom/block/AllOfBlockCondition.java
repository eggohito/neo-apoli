package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliBlockConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AllOfBlockCondition(List<BlockCondition> conditions) implements BlockCondition, AllOfMetaCondition<BlockCondition> {

	public static final MapCodec<AllOfBlockCondition> MAP_CODEC = MapCodecUtil.lazy(AllOfBlockCondition.class.getSimpleName(), () -> AllOfMetaCondition.mapCodec(BlockCondition.CODEC, AllOfBlockCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AllOfBlockCondition> STREAM_CODEC = StreamCodecUtil.lazy(AllOfBlockCondition.class.getSimpleName(), () -> AllOfMetaCondition.streamCodec(BlockCondition.STREAM_CODEC, AllOfBlockCondition::new));

	@Override
	public BlockCondition.Type<?> getType() {
		return NeoApoliBlockConditionTypes.ALL_OF;
	}

}
