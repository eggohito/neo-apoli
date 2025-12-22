package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceBlockCondition(ResourceLocation value) implements BlockCondition, IReferenceMetaCondition<BlockCondition> {

	public static final MapCodec<ReferenceBlockCondition> CODEC = IReferenceMetaCondition.createCodec(ReferenceBlockCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceBlockCondition> STREAM_CODEC = IReferenceMetaCondition.createStreamCodec(ReferenceBlockCondition::new);

	@Override
	public Pair<Class<BlockCondition>, String> classAndName() {
		return Pair.of(BlockCondition.class, "Block condition");
	}

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.REFERENCE;
	}

	@Override
	public String asDisplayString() {
		return BlockCondition.super.asDisplayString();
	}

}
