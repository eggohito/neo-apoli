package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.meta.OffsetMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Vec3d;

@EqualsAndHashCode
@Data
public final class OffsetBlockAction extends BlockAction implements OffsetMetaAction<BlockAction> {

	public static final MapCodec<OffsetBlockAction> CODEC = MapCodecUtil.lazy(OffsetBlockAction.class.getSimpleName(), () -> OffsetMetaAction.codec(BlockAction.CODEC, OffsetBlockAction::new));
	public static final PacketCodec<RegistryByteBuf, OffsetBlockAction> PACKET_CODEC = PacketCodecUtil.lazy(OffsetBlockAction.class.getSimpleName(), () -> OffsetMetaAction.packetCodec(BlockAction.PACKET_CODEC, OffsetBlockAction::new));

	private final BlockAction action;
	private final Vec3d offset;

	public OffsetBlockAction(BlockAction action, Vec3d offset) {
		this.action = action;
		this.offset = offset;
	}

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.OFFSET;
	}

	@Override
	public void impl(ServerContext context) {
		OffsetMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		OffsetMetaAction.super.validate(reporter);
	}

}
