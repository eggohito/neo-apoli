package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.meta.NothingMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode(callSuper = false)
@Data
public final class NothingBlockAction extends BlockAction implements NothingMetaAction {

	public static final MapCodec<NothingBlockAction> CODEC = NothingMetaAction.codec(NothingBlockAction::new);
	public static final PacketCodec<RegistryByteBuf, NothingBlockAction> PACKET_CODEC = NothingMetaAction.packetCodec(NothingBlockAction::new);

	public NothingBlockAction() {

	}

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.NOTHING;
	}

	@Override
	protected void impl(Context context) {

	}

	@Override
	public void validate(ErrorReporter reporter) {

	}

}
