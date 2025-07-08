package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class SetBlockBlockAction extends BlockAction {

	public static final MapCodec<SetBlockBlockAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.REGULAR_OR_STRINGIFIED_BLOCK_STATE.fieldOf("block").forGetter(SetBlockBlockAction::state)
	).apply(instance, SetBlockBlockAction::new));

	public static final PacketCodec<RegistryByteBuf, SetBlockBlockAction> PACKET_CODEC = PacketCodec.tuple(
		NeoApoliPacketCodecs.BLOCK_STATE, SetBlockBlockAction::state,
		SetBlockBlockAction::new
	);

	private final BlockState state;

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.SET_BLOCK;
	}

	@Override
	protected void impl(ServerContext context) {
		context.getWorld().setBlockState(this.getBlockPos(context), state());
	}

}
