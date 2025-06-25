package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.meta.SideMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode(callSuper = false)
@Data
public final class SideBlockAction extends BlockAction implements SideMetaAction<BlockAction> {

	public static final MapCodec<SideBlockAction> CODEC = NeoApoliMapCodecs.lazy(SideBlockAction.class.getSimpleName(), () -> SideMetaAction.codec(BlockAction.CODEC, SideBlockAction::new));
	public static final PacketCodec<RegistryByteBuf, SideBlockAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(SideBlockAction.class.getSimpleName(), () -> SideMetaAction.packetCodec(BlockAction.PACKET_CODEC, SideBlockAction::new));

	private final BlockAction action;
	private final Side side;

	public SideBlockAction(BlockAction action, Side side) {
		this.action = action;
		this.side = side;
	}

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.SIDE;
	}

	@Override
	public void impl(Context context) {
		SideMetaAction.super.impl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		SideMetaAction.super.validate(reporter);
	}

}
