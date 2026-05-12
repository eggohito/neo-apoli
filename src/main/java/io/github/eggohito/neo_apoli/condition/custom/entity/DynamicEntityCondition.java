package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.DynamicMetaCondition;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliEntityConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicEntityCondition(BooleanProvider value) implements EntityCondition, DynamicMetaCondition {

	public static final MapCodec<DynamicEntityCondition> MAP_CODEC = DynamicMetaCondition.mapCodec(DynamicEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicEntityCondition> STREAM_CODEC = DynamicMetaCondition.streamCodec(DynamicEntityCondition::new);

	@Override
	public EntityCondition.Type<?> getType() {
		return NeoApoliEntityConditionTypes.DYNAMIC;
	}

}
