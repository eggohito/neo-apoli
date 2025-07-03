package io.github.eggohito.neo_apoli.action.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

@EqualsAndHashCode
@Data
public final class ReferenceBiEntityAction extends BiEntityAction implements ReferenceMetaAction<BiEntityAction> {

	public static final MapCodec<ReferenceBiEntityAction> CODEC = ReferenceMetaAction.codec(ReferenceBiEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceBiEntityAction> PACKET_CODEC = ReferenceMetaAction.packetCodec(ReferenceBiEntityAction::new);

	private final Identifier value;

	public ReferenceBiEntityAction(Identifier value) {
		this.value = value;
	}

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.REFERENCE;
	}

	@Override
	public void impl(Context context) {
		ReferenceMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		ReferenceMetaAction.super.validate(reporter);
	}

}
