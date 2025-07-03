package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.ExecuteCommandMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class ExecuteCommandEntityAction extends EntityAction implements ExecuteCommandMetaAction {

	public static final MapCodec<ExecuteCommandEntityAction> CODEC = ExecuteCommandMetaAction.codec(ExecuteCommandEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, ExecuteCommandEntityAction> PACKET_CODEC = ExecuteCommandMetaAction.packetCodec(ExecuteCommandEntityAction::new);

	private final StringProvider command;

	public ExecuteCommandEntityAction(StringProvider command) {
		this.command = command;
	}

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.EXECUTE_COMMAND;
	}

	@Override
	public void impl(Context context) {
		ExecuteCommandMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		ExecuteCommandMetaAction.super.validate(reporter);
	}

}
