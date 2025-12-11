package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.DynamicMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicBlockCondition(BooleanProvider value) implements BlockCondition, DynamicMetaCondition {

	public static final MapCodec<DynamicBlockCondition> CODEC = DynamicMetaCondition.createCodec(DynamicBlockCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicBlockCondition> STREAM_CODEC = DynamicMetaCondition.createStreamCodec(DynamicBlockCondition::new);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.DYNAMIC;
	}

	@Override
	public String asDisplayString() {
		return BlockCondition.super.asDisplayString();
	}

}
