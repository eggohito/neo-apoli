package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IAnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AnyOfBiEntityCondition(List<BiEntityCondition> conditions) implements BiEntityCondition, IAnyOfMetaCondition<BiEntityCondition> {

	public static final MapCodec<AnyOfBiEntityCondition> MAP_CODEC = MapCodecUtil.lazy(AnyOfBiEntityCondition.class.getSimpleName(), () -> IAnyOfMetaCondition.mapCodec(BiEntityCondition.CODEC, AnyOfBiEntityCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AnyOfBiEntityCondition> STREAM_CODEC = StreamCodecUtil.lazy(AnyOfBiEntityCondition.class.getSimpleName(), () -> IAnyOfMetaCondition.streamCodec(BiEntityCondition.STREAM_CODEC, AnyOfBiEntityCondition::new));

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.ANY_OF;
	}

}
