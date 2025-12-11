package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.DynamicMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicCondition(BooleanProvider value) implements DynamicMetaCondition {

	public static final MapCodec<DynamicCondition> CODEC = DynamicMetaCondition.createCodec(DynamicCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicCondition> STREAM_CODEC = DynamicMetaCondition.createStreamCodec(DynamicCondition::new);

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.DYNAMIC;
	}

}
