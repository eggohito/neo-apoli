package io.github.eggohito.neo_apoli.action.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.meta.SequenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

@EqualsAndHashCode
@Data
public final class SequenceBiEntityAction extends BiEntityAction implements SequenceMetaAction<BiEntityAction> {

	public static final MapCodec<SequenceBiEntityAction> CODEC = MapCodecUtil.lazy(SequenceBiEntityAction.class.getSimpleName(), () -> SequenceMetaAction.codec(BiEntityAction.CODEC, SequenceBiEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, SequenceBiEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(SequenceBiEntityAction.class.getSimpleName(), () -> SequenceMetaAction.packetCodec(BiEntityAction.PACKET_CODEC, SequenceBiEntityAction::new));

	private final List<BiEntityAction> actions;

	public SequenceBiEntityAction(List<BiEntityAction> actions) {
		this.actions = actions;
	}

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.SEQUENCE;
	}

	@Override
	public void impl(Context context) {
		SequenceMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		SequenceMetaAction.super.validate(reporter);
	}

}
