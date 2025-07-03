package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.meta.IfElseMetaAction;
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

import java.util.Optional;

@EqualsAndHashCode
@Data
public final class IfElseBlockAction extends BlockAction implements IfElseMetaAction<BlockAction, BlockCondition> {

	public static final MapCodec<IfElseBlockAction> CODEC = NeoApoliMapCodecs.lazy(IfElseBlockAction.class.getSimpleName(), () -> IfElseMetaAction.codec(BlockCondition.CODEC, BlockAction.CODEC, IfElseBlockAction::new));
	public static final PacketCodec<RegistryByteBuf, IfElseBlockAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(IfElseBlockAction.class.getSimpleName(), () -> IfElseMetaAction.packetCodec(BlockCondition.PACKET_CODEC, BlockAction.PACKET_CODEC, IfElseBlockAction::new));

	private final BlockCondition condition;

	private final BlockAction ifAction;
	private final Optional<BlockAction> elseAction;

	public IfElseBlockAction(BlockCondition condition, BlockAction ifAction, Optional<BlockAction> elseAction) {
		this.condition = condition;
		this.ifAction = ifAction;
		this.elseAction = elseAction;
	}

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.IF_ELSE;
	}

	@Override
	public void impl(ServerContext context) {
		IfElseMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		IfElseMetaAction.super.validate(reporter);
	}

}
