package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IDynamicMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicBlockCondition(BooleanProvider value) implements BlockCondition, IDynamicMetaCondition {

	public static final MapCodec<DynamicBlockCondition> MAP_CODEC = IDynamicMetaCondition.mapCodec(DynamicBlockCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicBlockCondition> STREAM_CODEC = IDynamicMetaCondition.streamCodec(DynamicBlockCondition::new);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.DYNAMIC;
	}

}
