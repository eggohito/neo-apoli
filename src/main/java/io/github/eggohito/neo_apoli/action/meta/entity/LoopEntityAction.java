package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.LoopMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
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
public final class LoopEntityAction extends EntityAction implements LoopMetaAction<EntityAction> {

	public static final MapCodec<LoopEntityAction> CODEC = MapCodecUtil.lazy(LoopEntityAction.class.getSimpleName(), () -> LoopMetaAction.codec(EntityAction.CODEC, LoopEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, LoopEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(LoopEntityAction.class.getSimpleName(), () -> LoopMetaAction.packetCodec(EntityAction.PACKET_CODEC, LoopEntityAction::new));

	private final Optional<EntityAction> beforeAction;
	private final Optional<EntityAction> afterAction;

	private final NumberProvider iterations;
	private final EntityAction action;

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.LOOP;
	}

	@Override
	protected void impl(Context context) {
		LoopMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		LoopMetaAction.super.validate(reporter);
	}

}
