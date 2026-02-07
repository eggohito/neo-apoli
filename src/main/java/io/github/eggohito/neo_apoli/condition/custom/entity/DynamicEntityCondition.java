package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IDynamicMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicEntityCondition(BooleanProvider value) implements EntityCondition, IDynamicMetaCondition {

	public static final MapCodec<DynamicEntityCondition> MAP_CODEC = IDynamicMetaCondition.mapCodec(DynamicEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicEntityCondition> STREAM_CODEC = IDynamicMetaCondition.streamCodec(DynamicEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.DYNAMIC;
	}

}
