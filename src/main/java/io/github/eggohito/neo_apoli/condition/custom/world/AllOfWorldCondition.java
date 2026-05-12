package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliWorldConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AllOfWorldCondition(List<WorldCondition> conditions) implements WorldCondition, AllOfMetaCondition<WorldCondition> {

	public static final MapCodec<AllOfWorldCondition> MAP_CODEC = MapCodecUtil.lazy(AllOfWorldCondition.class.getSimpleName(), () -> AllOfMetaCondition.mapCodec(WorldCondition.CODEC, AllOfWorldCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AllOfWorldCondition> STREAM_CODEC = StreamCodecUtil.lazy(AllOfWorldCondition.class.getSimpleName(), () -> AllOfMetaCondition.streamCodec(WorldCondition.STREAM_CODEC, AllOfWorldCondition::new));

	@Override
	public WorldCondition.Type<?> getType() {
		return NeoApoliWorldConditionTypes.ALL_OF;
	}

}
