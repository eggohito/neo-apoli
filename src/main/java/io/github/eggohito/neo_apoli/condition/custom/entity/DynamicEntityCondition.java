package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.DynamicMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicEntityCondition(BooleanProvider value) implements EntityCondition, DynamicMetaCondition {

	public static final MapCodec<DynamicEntityCondition> CODEC = DynamicMetaCondition.createCodec(DynamicEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicEntityCondition> STREAM_CODEC = DynamicMetaCondition.createStreamCodec(DynamicEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.DYNAMIC;
	}

	@Override
	public String asDisplayString() {
		return EntityCondition.super.asDisplayString();
	}

}
