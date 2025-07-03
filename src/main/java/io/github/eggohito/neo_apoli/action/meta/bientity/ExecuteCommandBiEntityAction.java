package io.github.eggohito.neo_apoli.action.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.meta.ExecuteCommandMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class ExecuteCommandBiEntityAction extends BiEntityAction implements ExecuteCommandMetaAction {

	public static final MapCodec<ExecuteCommandBiEntityAction> CODEC = ExecuteCommandMetaAction.codec(ExecuteCommandBiEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, ExecuteCommandBiEntityAction> PACKET_CODEC = ExecuteCommandMetaAction.packetCodec(ExecuteCommandBiEntityAction::new);

	private final StringProvider command;

	public ExecuteCommandBiEntityAction(StringProvider command) {
		this.command = command;
	}

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.EXECUTE_COMMAND;
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
