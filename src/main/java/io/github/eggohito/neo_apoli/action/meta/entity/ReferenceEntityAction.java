package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

@EqualsAndHashCode(callSuper = false)
@Data
public final class ReferenceEntityAction extends EntityAction implements ReferenceMetaAction<EntityAction> {

	public static final MapCodec<ReferenceEntityAction> CODEC = ReferenceMetaAction.codec(ReferenceEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceEntityAction> PACKET_CODEC = ReferenceMetaAction.packetCodec(ReferenceEntityAction::new);

	private final Identifier value;

	public ReferenceEntityAction(Identifier value) {
		this.value = value;
	}

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.REFERENCE;
	}

	@Override
	public void impl(Context context) {
		ReferenceMetaAction.super.impl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		ReferenceMetaAction.super.validate(reporter);
	}

}
