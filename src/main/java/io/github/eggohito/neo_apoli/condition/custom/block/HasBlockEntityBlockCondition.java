package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode(callSuper = false)
@Data
public final class HasBlockEntityBlockCondition extends BlockCondition {

	public static final MapCodec<HasBlockEntityBlockCondition> CODEC = MapCodec.unit(HasBlockEntityBlockCondition::new);
	public static final PacketCodec<RegistryByteBuf, HasBlockEntityBlockCondition> PACKET_CODEC = PacketCodec.unit(new HasBlockEntityBlockCondition());

	public HasBlockEntityBlockCondition() {

	}

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.HAS_BLOCK_ENTITY;
	}

	@Override
	protected boolean impl(Context context) {
		return this.getBlockState(context).hasBlockEntity();
	}

}
