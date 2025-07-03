package io.github.eggohito.neo_apoli.action.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.meta.NothingMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class NothingBiEntityAction extends BiEntityAction implements NothingMetaAction {

	public static final MapCodec<NothingBiEntityAction> CODEC = NothingMetaAction.codec(NothingBiEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, NothingBiEntityAction> PACKET_CODEC = NothingMetaAction.packetCodec(NothingBiEntityAction::new);

	public NothingBiEntityAction() {

	}

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.NOTHING;
	}

	@Override
	protected void impl(Context context) {

	}

	@Override
	public void validate(ErrorReporter reporter) {

	}

}
