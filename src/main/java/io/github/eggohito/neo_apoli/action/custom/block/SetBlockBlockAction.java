package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

@EqualsAndHashCode(callSuper = false)
@Data
public final class SetBlockBlockAction extends BlockAction {

	public static final MapCodec<SetBlockBlockAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockState.CODEC.fieldOf("block").forGetter(SetBlockBlockAction::state)
	).apply(instance, SetBlockBlockAction::new));

	public static final PacketCodec<RegistryByteBuf, SetBlockBlockAction> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.registryCodec(BlockState.CODEC), SetBlockBlockAction::state,
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
