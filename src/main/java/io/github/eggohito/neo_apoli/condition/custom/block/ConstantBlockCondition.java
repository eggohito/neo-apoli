package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ConstantBlockCondition(boolean value) implements BlockCondition, ConstantMetaCondition {

	public static final Codec<ConstantBlockCondition> INLINE_CODEC = ConstantMetaCondition.inlineCodec(ConstantBlockCondition::new);

	public static final MapCodec<ConstantBlockCondition> CODEC = ConstantMetaCondition.codec(ConstantBlockCondition::new);

	public static final PacketCodec<RegistryByteBuf, ConstantBlockCondition> PACKET_CODEC = ConstantMetaCondition.packetCodec(ConstantBlockCondition::new);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.CONSTANT;
	}

	@Override
	public String asDisplayString() {
		return BlockCondition.super.asDisplayString();
	}

}
