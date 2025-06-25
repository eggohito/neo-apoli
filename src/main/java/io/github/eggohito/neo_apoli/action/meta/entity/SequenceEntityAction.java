package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.SequenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
public final class SequenceEntityAction extends EntityAction implements SequenceMetaAction<EntityAction> {

	public static final MapCodec<SequenceEntityAction> CODEC = NeoApoliMapCodecs.lazy(SequenceEntityAction.class.getSimpleName(), () -> SequenceMetaAction.codec(EntityAction.CODEC, SequenceEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, SequenceEntityAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(SequenceEntityAction.class.getSimpleName(), () -> SequenceMetaAction.packetCodec(EntityAction.PACKET_CODEC, SequenceEntityAction::new));

	private final List<EntityAction> actions;

	public SequenceEntityAction(List<EntityAction> actions) {
		this.actions = actions;
	}

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.SEQUENCE;
	}

	@Override
	public void impl(Context context) {
		SequenceMetaAction.super.impl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		SequenceMetaAction.super.validate(reporter);
	}

}
