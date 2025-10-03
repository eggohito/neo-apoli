package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.OffsetMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Vec3d;

@EqualsAndHashCode
@Data
public final class OffsetEntityAction extends EntityAction implements OffsetMetaAction<EntityAction> {

	public static final MapCodec<OffsetEntityAction> CODEC = MapCodecUtil.lazy(OffsetEntityAction.class.getSimpleName(), () -> OffsetMetaAction.codec(EntityAction.CODEC, OffsetEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, OffsetEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(OffsetEntityAction.class.getSimpleName(), () -> OffsetMetaAction.packetCodec(EntityAction.PACKET_CODEC, OffsetEntityAction::new));

	private final EntityAction action;
	private final Vec3d offset;

	public OffsetEntityAction(EntityAction action, Vec3d offset) {
		this.action = action;
		this.offset = offset;
	}

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.OFFSET;
	}

	@Override
	public void impl(Context context) {

		Vec3d offsetPos = context.required(ContextParameters.ENTITY_POS).add(this.offset());
		context = new ContextImpl.Builder(context)
			.add(ContextParameters.ENTITY_POS, offsetPos)
			.build(context.getWorld());

		this.action().execute(context.makeChild(".action"));

	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		OffsetMetaAction.super.validate(reporter);
	}

}
