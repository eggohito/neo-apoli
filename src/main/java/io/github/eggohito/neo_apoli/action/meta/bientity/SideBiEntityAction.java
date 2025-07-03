package io.github.eggohito.neo_apoli.action.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.meta.SideMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class SideBiEntityAction extends BiEntityAction implements SideMetaAction<BiEntityAction> {

	public static final MapCodec<SideBiEntityAction> CODEC = NeoApoliMapCodecs.lazy(SideBiEntityAction.class.getSimpleName(), () -> SideMetaAction.codec(BiEntityAction.CODEC, SideBiEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, SideBiEntityAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(SideBiEntityAction.class.getSimpleName(), () -> SideMetaAction.packetCodec(BiEntityAction.PACKET_CODEC, SideBiEntityAction::new));

	private final BiEntityAction action;
	private final Side side;

	public SideBiEntityAction(BiEntityAction action, Side side) {
		this.action = action;
		this.side = side;
	}

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.SIDE;
	}

	@Override
	public void impl(Context context) {
		SideMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		SideMetaAction.super.validate(reporter);
	}

}
