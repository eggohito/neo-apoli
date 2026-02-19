package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AnyOfCondition(List<Condition> conditions) implements AnyOfMetaCondition<Condition> {

	public static final MapCodec<AnyOfCondition> MAP_CODEC = MapCodecUtil.lazy(AnyOfCondition.class.getSimpleName(), () -> AnyOfMetaCondition.mapCodec(Condition.CODEC, AnyOfCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AnyOfCondition> STREAM_CODEC = StreamCodecUtil.lazy(AnyOfCondition.class.getSimpleName(), () -> AnyOfMetaCondition.streamCodec(Condition.STREAM_CODEC, AnyOfCondition::new));

	@Override
	public ConditionType<?> getType() {
		return ConditionTypes.ANY_OF;
	}

}
