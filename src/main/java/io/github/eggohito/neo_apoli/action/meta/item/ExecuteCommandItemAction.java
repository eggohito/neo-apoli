package io.github.eggohito.neo_apoli.action.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.meta.ExecuteCommandMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class ExecuteCommandItemAction extends ItemAction implements ExecuteCommandMetaAction {

	public static final MapCodec<ExecuteCommandItemAction> CODEC = ExecuteCommandMetaAction.codec(ExecuteCommandItemAction::new);
	public static final PacketCodec<RegistryByteBuf, ExecuteCommandItemAction> PACKET_CODEC = ExecuteCommandMetaAction.packetCodec(ExecuteCommandItemAction::new);

	private final StringProvider command;

	public ExecuteCommandItemAction(StringProvider command) {
		this.command = command;
	}

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.EXECUTE_COMMAND;
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
