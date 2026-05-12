package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliWorldConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AnyOfWorldCondition(List<WorldCondition> conditions) implements WorldCondition, AnyOfMetaCondition<WorldCondition> {

	public static final MapCodec<AnyOfWorldCondition> MAP_CODEC = MapCodecUtil.lazy(AnyOfWorldCondition.class.getSimpleName(), () -> AnyOfMetaCondition.mapCodec(WorldCondition.CODEC, AnyOfWorldCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AnyOfWorldCondition> STREAM_CODEC = StreamCodecUtil.lazy(AnyOfWorldCondition.class.getSimpleName(), () -> AnyOfMetaCondition.streamCodec(WorldCondition.STREAM_CODEC, AnyOfWorldCondition::new));

	@Override
	public WorldCondition.Type<?> getType() {
		return NeoApoliWorldConditionTypes.ANY_OF;
	}

}
