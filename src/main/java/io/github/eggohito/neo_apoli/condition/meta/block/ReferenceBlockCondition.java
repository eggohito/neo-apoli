package io.github.eggohito.neo_apoli.condition.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import io.github.eggohito.neo_apoli.condition.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record ReferenceBlockCondition(Identifier value) implements BlockCondition, ReferenceMetaCondition<BlockCondition, BlockConditionType<?>> {

	public static final MapCodec<ReferenceBlockCondition> CODEC = ReferenceMetaCondition.codec(ReferenceBlockCondition::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceBlockCondition> PACKET_CODEC = ReferenceMetaCondition.packetCodec(ReferenceBlockCondition::new);

	@Override
	public ConditionCategory<BlockCondition> getCategory() {
		return BlockCondition.super.getCategory();
	}

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.REFERENCE;
	}

}
