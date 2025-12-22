package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicMetaCondition(BooleanProvider value) implements IDynamicMetaCondition {

	public static final MapCodec<DynamicMetaCondition> CODEC = IDynamicMetaCondition.createCodec(DynamicMetaCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicMetaCondition> STREAM_CODEC = IDynamicMetaCondition.createStreamCodec(DynamicMetaCondition::new);

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.DYNAMIC;
	}

}
