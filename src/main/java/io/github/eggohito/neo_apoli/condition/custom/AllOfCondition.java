package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record AllOfCondition(List<Condition> conditions) implements AllOfMetaCondition<Condition> {

	public static final MapCodec<AllOfCondition> CODEC = MapCodecUtil.lazy(AllOfCondition.class.getSimpleName(), () -> AllOfMetaCondition.createCodec(Condition.CODEC, AllOfCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AllOfCondition> STREAM_CODEC = StreamCodecUtil.lazy(AllOfCondition.class.getSimpleName(), () -> AllOfMetaCondition.createStreamCodec(Condition.STREAM_CODEC, AllOfCondition::new));

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.ALL_OF;
	}

}
