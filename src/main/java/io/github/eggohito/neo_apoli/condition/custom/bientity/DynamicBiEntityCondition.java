package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.DynamicMetaCondition;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliBiEntityConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicBiEntityCondition(BooleanProvider value) implements BiEntityCondition, DynamicMetaCondition {

	public static final MapCodec<DynamicBiEntityCondition> MAP_CODEC = DynamicMetaCondition.mapCodec(DynamicBiEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicBiEntityCondition> STREAM_CODEC = DynamicMetaCondition.streamCodec(DynamicBiEntityCondition::new);

	@Override
	public BiEntityCondition.Type<?> getType() {
		return NeoApoliBiEntityConditionTypes.DYNAMIC;
	}

}
