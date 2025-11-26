package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record AnyOfCondition(List<Condition> conditions) implements AnyOfMetaCondition<Condition> {

	public static final MapCodec<AnyOfCondition> CODEC = MapCodecUtil.lazy(AnyOfCondition.class.getSimpleName(), () -> AnyOfMetaCondition.createCodec(Condition.CODEC, AnyOfCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AnyOfCondition> STREAM_CODEC = StreamCodecUtil.lazy(AnyOfCondition.class.getSimpleName(), () -> AnyOfMetaCondition.createStreamCodec(Condition.STREAM_CODEC, AnyOfCondition::new));

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.ANY_OF;
	}

}
