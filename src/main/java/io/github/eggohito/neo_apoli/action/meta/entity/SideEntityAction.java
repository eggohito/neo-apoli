package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.SideMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class SideEntityAction extends EntityAction implements SideMetaAction<EntityAction> {

	public static final MapCodec<SideEntityAction> CODEC = MapCodecUtil.lazy(SideEntityAction.class.getSimpleName(), () -> SideMetaAction.codec(EntityAction.CODEC, SideEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, SideEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(SideEntityAction.class.getSimpleName(), () -> SideMetaAction.packetCodec(EntityAction.PACKET_CODEC, SideEntityAction::new));

	private final EntityAction action;
	private final Side side;

	public SideEntityAction(EntityAction action, Side side) {
		this.action = action;
		this.side = side;
	}

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.SIDE;
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
