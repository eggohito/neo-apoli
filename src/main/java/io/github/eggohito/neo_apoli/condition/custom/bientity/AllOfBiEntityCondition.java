package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AllOfBiEntityCondition(List<BiEntityCondition> conditions) implements BiEntityCondition, AllOfMetaCondition<BiEntityCondition> {

	public static final MapCodec<AllOfBiEntityCondition> MAP_CODEC = MapCodecUtil.lazy(AllOfBiEntityCondition.class.getSimpleName(), () -> AllOfMetaCondition.mapCodec(BiEntityCondition.CODEC, AllOfBiEntityCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AllOfBiEntityCondition> STREAM_CODEC = StreamCodecUtil.lazy(AllOfBiEntityCondition.class.getSimpleName(), () -> AllOfMetaCondition.streamCodec(BiEntityCondition.STREAM_CODEC, AllOfBiEntityCondition::new));

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.ALL_OF;
	}

}
