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

public record AllOfMetaCondition(List<Condition> conditions) implements IAllOfMetaCondition<Condition> {

	public static final MapCodec<AllOfMetaCondition> CODEC = MapCodecUtil.lazy(AllOfMetaCondition.class.getSimpleName(), () -> IAllOfMetaCondition.createCodec(Condition.CODEC, AllOfMetaCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AllOfMetaCondition> STREAM_CODEC = StreamCodecUtil.lazy(AllOfMetaCondition.class.getSimpleName(), () -> IAllOfMetaCondition.createStreamCodec(Condition.STREAM_CODEC, AllOfMetaCondition::new));

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.ALL_OF;
	}

}
