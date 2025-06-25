package io.github.eggohito.neo_apoli.condition.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode(callSuper = false)
@Data
public final class ConstantBlockCondition extends BlockCondition implements ConstantMetaCondition {

	public static final MapCodec<ConstantBlockCondition> CODEC = ConstantMetaCondition.codec(ConstantBlockCondition::new);
	public static final PacketCodec<RegistryByteBuf, ConstantBlockCondition> PACKET_CODEC = ConstantMetaCondition.packetCodec(ConstantBlockCondition::new).cast();

	private final boolean value;

	public ConstantBlockCondition(boolean value) {
		this.value = value;
	}

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.CONSTANT;
	}

	@Override
	protected boolean impl(Context context) {
		return value();
	}

}
