package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.ExplodeMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public record ExplodeEntityAction(BiEntityCondition damageableBiEntityCondition, BlockCondition destructibleBlockCondition, ExplosionProperty property, ExplosionDisplay display) implements EntityAction, ExplodeMetaAction<EntityActionType<?>> {

	public static final MapCodec<ExplodeEntityAction> CODEC = ExplodeMetaAction.codec(ExplodeEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, ExplodeEntityAction> PACKET_CODEC = ExplodeMetaAction.packetCodec(ExplodeEntityAction::new);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.EXPLODE;
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return EntityAction.super.getAllowedParameters();
	}

}
