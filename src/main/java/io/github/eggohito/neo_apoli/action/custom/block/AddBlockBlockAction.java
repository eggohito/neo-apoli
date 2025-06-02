package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public record AddBlockBlockAction(BlockState state) implements BlockAction {

	public static final MapCodec<AddBlockBlockAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockState.CODEC.fieldOf("block").forGetter(AddBlockBlockAction::state)
	).apply(instance, AddBlockBlockAction::new));

	public static final PacketCodec<RegistryByteBuf, AddBlockBlockAction> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.registryCodec(BlockState.CODEC), AddBlockBlockAction::state,
		AddBlockBlockAction::new
	);

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.ADD_BLOCK;
	}

	@Override
	public void execute(Context context) {

		if (!(context.getWorld() instanceof ServerWorld serverWorld)) {
			return;
		}

		BlockPos pos = BlockPos.ofFloored(context.required(ContextParameters.POSITION));
		context.optional(ContextParameters.DIRECTION)
			.map(pos::offset)
			.ifPresent(offsetPos -> serverWorld.setBlockState(offsetPos, state()));

	}

}
