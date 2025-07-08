package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.meta.SequenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

@EqualsAndHashCode
@Data
public final class SequenceBlockAction extends BlockAction implements SequenceMetaAction<BlockAction> {

	public static final MapCodec<SequenceBlockAction> CODEC = MapCodecUtil.lazy(SequenceBlockAction.class.getSimpleName(), () -> SequenceMetaAction.codec(BlockAction.CODEC, SequenceBlockAction::new));
	public static final PacketCodec<RegistryByteBuf, SequenceBlockAction> PACKET_CODEC = PacketCodecUtil.lazy(SequenceBlockAction.class.getSimpleName(), () -> SequenceMetaAction.packetCodec(BlockAction.PACKET_CODEC, SequenceBlockAction::new));

	private final List<BlockAction> actions;

	public SequenceBlockAction(List<BlockAction> actions) {
		this.actions = actions;
	}

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.SEQUENCE;
	}

	@Override
	public void impl(ServerContext context) {
		SequenceMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		SequenceMetaAction.super.validate(reporter);
	}

}
