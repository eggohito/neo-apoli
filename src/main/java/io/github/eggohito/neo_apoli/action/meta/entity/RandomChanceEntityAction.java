package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.RandomChanceMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
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
public final class RandomChanceEntityAction extends EntityAction implements RandomChanceMetaAction<EntityAction> {

	public static final MapCodec<RandomChanceEntityAction> CODEC = MapCodecUtil.lazy(RandomChanceEntityAction.class.getSimpleName(), () -> RandomChanceMetaAction.codec(EntityAction.CODEC, RandomChanceEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, RandomChanceEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(RandomChanceEntityAction.class.getSimpleName(), () -> RandomChanceMetaAction.packetCodec(EntityAction.PACKET_CODEC, RandomChanceEntityAction::new));

	private final EntityAction successAction;
	private final Optional<EntityAction> failAction;

	private final float chance;

	public RandomChanceEntityAction(EntityAction successAction, Optional<EntityAction> failAction, float chance) {
		this.successAction = successAction;
		this.failAction = failAction;
		this.chance = chance;
	}

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.RANDOM_CHANCE;
	}

	@Override
	public void impl(Context context) {
		RandomChanceMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		RandomChanceMetaAction.super.validate(reporter);
	}

}
