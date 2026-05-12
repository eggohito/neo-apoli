package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AllOfCondition(List<Condition> conditions) implements AllOfMetaCondition<Condition> {

	public static final MapCodec<AllOfCondition> MAP_CODEC = MapCodecUtil.lazy(AllOfCondition.class.getSimpleName(), () -> AllOfMetaCondition.mapCodec(Condition.CODEC, AllOfCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AllOfCondition> STREAM_CODEC = StreamCodecUtil.lazy(AllOfCondition.class.getSimpleName(), () -> AllOfMetaCondition.streamCodec(Condition.STREAM_CODEC, AllOfCondition::new));

	@Override
	public Type<?> getType() {
		return NeoApoliConditionTypes.ALL_OF;
	}

}
