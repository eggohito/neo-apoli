package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.meta.LoopMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

@EqualsAndHashCode
@Data
public final class LoopBlockAction extends BlockAction implements LoopMetaAction<BlockAction> {

	public static final MapCodec<LoopBlockAction> CODEC = MapCodecUtil.lazy(LoopBlockAction.class.getSimpleName(), () -> LoopMetaAction.codec(BlockAction.CODEC, LoopBlockAction::new));
	public static final PacketCodec<RegistryByteBuf, LoopBlockAction> PACKET_CODEC = PacketCodecUtil.lazy(LoopBlockAction.class.getSimpleName(), () -> LoopMetaAction.packetCodec(BlockAction.PACKET_CODEC, LoopBlockAction::new));

	private final Optional<BlockAction> beforeAction;
	private final Optional<BlockAction> afterAction;

	private final NumberProvider iterations;
	private final BlockAction action;

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.LOOP;
	}

	@Override
	protected void impl(ServerContext context) {
		LoopMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		LoopMetaAction.super.validate(reporter);
	}

}
