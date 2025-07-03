package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.meta.IfElseListMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

@EqualsAndHashCode
@Data
public final class IfElseListBlockAction extends BlockAction implements IfElseListMetaAction<BlockAction, BlockCondition> {

	public static final MapCodec<IfElseListBlockAction> CODEC = NeoApoliMapCodecs.lazy(IfElseListBlockAction.class.getSimpleName(), () -> IfElseListMetaAction.codec(BlockCondition.CODEC, BlockAction.CODEC, IfElseListBlockAction::new));
	public static final PacketCodec<RegistryByteBuf, IfElseListBlockAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(IfElseListBlockAction.class.getSimpleName(), () -> IfElseListMetaAction.packetCodec(BlockCondition.PACKET_CODEC, BlockAction.PACKET_CODEC, IfElseListBlockAction::new));

	private final List<Entry<BlockCondition, BlockAction>> entries;

	public IfElseListBlockAction(List<Entry<BlockCondition, BlockAction>> entries) {
		this.entries = entries;
	}

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.IF_ELSE_LIST;
	}

	@Override
	public void impl(ServerContext context) {
		IfElseListMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		IfElseListMetaAction.super.validate(reporter);
	}

}
