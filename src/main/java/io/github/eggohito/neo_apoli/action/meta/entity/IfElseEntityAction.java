package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.IfElseMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

@EqualsAndHashCode
@Data
public final class IfElseEntityAction extends EntityAction implements IfElseMetaAction<EntityAction, EntityCondition> {

	public static final MapCodec<IfElseEntityAction> CODEC = MapCodecUtil.lazy(IfElseEntityAction.class.getSimpleName(), () -> IfElseMetaAction.codec(EntityCondition.CODEC, EntityAction.CODEC, IfElseEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, IfElseEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(IfElseEntityAction.class.getSimpleName(), () -> IfElseMetaAction.packetCodec(EntityCondition.PACKET_CODEC, EntityAction.PACKET_CODEC, IfElseEntityAction::new));

	private final EntityCondition condition;

	private final EntityAction ifAction;
	private final Optional<EntityAction> elseAction;

	public IfElseEntityAction(EntityCondition condition, EntityAction ifAction, Optional<EntityAction> elseAction) {
		this.condition = condition;
		this.ifAction = ifAction;
		this.elseAction = elseAction;
	}

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.IF_ELSE;
	}

	@Override
	public void impl(Context context) {
		IfElseMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		IfElseMetaAction.super.validate(reporter);
	}

}
