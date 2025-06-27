package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockPos;

@EqualsAndHashCode(callSuper = false)
@Data
public final class AddBlockBlockAction extends BlockAction {

	public static final MapCodec<AddBlockBlockAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockState.CODEC.fieldOf("block").forGetter(AddBlockBlockAction::state)
	).apply(instance, AddBlockBlockAction::new));

	public static final PacketCodec<RegistryByteBuf, AddBlockBlockAction> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.registryCodec(BlockState.CODEC), AddBlockBlockAction::state,
		AddBlockBlockAction::new
	);

	private final BlockState state;

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.ADD_BLOCK;
	}

	@Override
	protected void impl(ServerContext context) {
		BlockPos pos = this.getBlockPos(context);
		context.optional(ContextParameters.DIRECTION)
			.map(pos::offset)
			.ifPresent(offsetPos -> context.getWorld().setBlockState(offsetPos, state()));
	}

}
