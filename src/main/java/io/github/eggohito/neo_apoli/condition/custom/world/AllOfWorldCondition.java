package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IAllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionType;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AllOfWorldCondition(List<WorldCondition> conditions) implements WorldCondition, IAllOfMetaCondition<WorldCondition> {

	public static final MapCodec<AllOfWorldCondition> CODEC = MapCodecUtil.lazy(AllOfWorldCondition.class.getSimpleName(), () -> IAllOfMetaCondition.createCodec(WorldCondition.CODEC, AllOfWorldCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AllOfWorldCondition> STREAM_CODEC = StreamCodecUtil.lazy(AllOfWorldCondition.class.getSimpleName(), () -> IAllOfMetaCondition.createStreamCodec(WorldCondition.STREAM_CODEC, AllOfWorldCondition::new));

	@Override
	public WorldConditionType<?> getType() {
		return WorldConditionTypes.ALL_OF;
	}

	@Override
	public String asDisplayString() {
		return WorldCondition.super.asDisplayString();
	}

}
