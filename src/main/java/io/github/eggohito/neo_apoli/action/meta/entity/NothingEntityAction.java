package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.NothingMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class NothingEntityAction extends EntityAction implements NothingMetaAction {

	public static final MapCodec<NothingEntityAction> CODEC = NothingMetaAction.codec(NothingEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, NothingEntityAction> PACKET_CODEC = NothingMetaAction.packetCodec(NothingEntityAction::new);

	public NothingEntityAction() {

	}

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.NOTHING;
	}

	@Override
	protected void impl(Context context) {

	}

	@Override
	public void validate(ErrorReporter reporter) {

	}

}
