package io.github.eggohito.neo_apoli.action.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.meta.ExecuteCommandMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ExecuteCommandBiEntityAction(StringProvider command) implements BiEntityAction, ExecuteCommandMetaAction<BiEntityActionType<?>> {

	public static final MapCodec<ExecuteCommandBiEntityAction> CODEC = ExecuteCommandMetaAction.codec(ExecuteCommandBiEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, ExecuteCommandBiEntityAction> PACKET_CODEC = ExecuteCommandMetaAction.packetCodec(ExecuteCommandBiEntityAction::new);

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.EXECUTE_COMMAND;
	}

}
