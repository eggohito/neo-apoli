package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.DynamicMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicCondition(BooleanProvider value) implements DynamicMetaCondition {

	public static final MapCodec<DynamicCondition> MAP_CODEC = DynamicMetaCondition.mapCodec(DynamicCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicCondition> STREAM_CODEC = DynamicMetaCondition.streamCodec(DynamicCondition::new);

	@Override
	public ConditionType<?> getType() {
		return ConditionTypes.DYNAMIC;
	}

}
