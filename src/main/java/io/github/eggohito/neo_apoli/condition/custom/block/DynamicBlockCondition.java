package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.DynamicMetaCondition;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliBlockConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicBlockCondition(BooleanProvider value) implements BlockCondition, DynamicMetaCondition {

	public static final MapCodec<DynamicBlockCondition> MAP_CODEC = DynamicMetaCondition.mapCodec(DynamicBlockCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicBlockCondition> STREAM_CODEC = DynamicMetaCondition.streamCodec(DynamicBlockCondition::new);

	@Override
	public BlockCondition.Type<?> getType() {
		return NeoApoliBlockConditionTypes.DYNAMIC;
	}

}
