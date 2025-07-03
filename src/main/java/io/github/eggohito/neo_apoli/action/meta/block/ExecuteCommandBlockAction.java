package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.meta.ExecuteCommandMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class ExecuteCommandBlockAction extends BlockAction implements ExecuteCommandMetaAction {

	public static final MapCodec<ExecuteCommandBlockAction> CODEC = ExecuteCommandMetaAction.codec(ExecuteCommandBlockAction::new);
	public static final PacketCodec<RegistryByteBuf, ExecuteCommandBlockAction> PACKET_CODEC = ExecuteCommandMetaAction.packetCodec(ExecuteCommandBlockAction::new);

	private final StringProvider command;

	public ExecuteCommandBlockAction(StringProvider command) {
		this.command = command;
	}

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.EXECUTE_COMMAND;
	}

	@Override
	public void impl(ServerContext context) {
		ExecuteCommandMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		ExecuteCommandMetaAction.super.validate(reporter);
	}

}
