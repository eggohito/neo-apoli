package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record ReferenceBlockCondition(Identifier value) implements BlockCondition, ReferenceMetaCondition<BlockCondition> {

	public static final MapCodec<ReferenceBlockCondition> CODEC = ReferenceMetaCondition.codec(ReferenceBlockCondition::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceBlockCondition> PACKET_CODEC = ReferenceMetaCondition.packetCodec(ReferenceBlockCondition::new);

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
