package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AnyOfMetaCondition(List<Condition> conditions) implements IAnyOfMetaCondition<Condition> {

	public static final MapCodec<AnyOfMetaCondition> MAP_CODEC = MapCodecUtil.lazy(AnyOfMetaCondition.class.getSimpleName(), () -> IAnyOfMetaCondition.mapCodec(Condition.CODEC, AnyOfMetaCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AnyOfMetaCondition> STREAM_CODEC = StreamCodecUtil.lazy(AnyOfMetaCondition.class.getSimpleName(), () -> IAnyOfMetaCondition.streamCodec(Condition.STREAM_CODEC, AnyOfMetaCondition::new));

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.ANY_OF;
	}

}
