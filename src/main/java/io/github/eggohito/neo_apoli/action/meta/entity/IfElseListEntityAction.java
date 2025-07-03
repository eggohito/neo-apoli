package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.IfElseListMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

@EqualsAndHashCode
@Data
public final class IfElseListEntityAction extends EntityAction implements IfElseListMetaAction<EntityAction, EntityCondition> {

	public static final MapCodec<IfElseListEntityAction> CODEC = NeoApoliMapCodecs.lazy(IfElseListEntityAction.class.getSimpleName(), () -> IfElseListMetaAction.codec(EntityCondition.CODEC, EntityAction.CODEC, IfElseListEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, IfElseListEntityAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(IfElseListEntityAction.class.getSimpleName(), () -> IfElseListMetaAction.packetCodec(EntityCondition.PACKET_CODEC, EntityAction.PACKET_CODEC, IfElseListEntityAction::new));

	private final List<Entry<EntityCondition, EntityAction>> entries;

	public IfElseListEntityAction(List<Entry<EntityCondition, EntityAction>> entries) {
		this.entries = entries;
	}

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.IF_ELSE_LIST;
	}

	@Override
	public void impl(Context context) {
		IfElseListMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		IfElseListMetaAction.super.validate(reporter);
	}

}
