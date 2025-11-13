package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public interface EntityAction extends Action {

	Codec<EntityAction> CODEC = Codec.recursive(EntityAction.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(EntityActionType.CODEC.dispatch(EntityAction::getType, EntityActionType::mapCodec), codec.listOf().xmap(SequenceEntityAction::new, SequenceEntityAction::actions), NothingEntityAction.INLINE_CODEC));

	PacketCodec<RegistryByteBuf, EntityAction> PACKET_CODEC = EntityActionType.PACKET_CODEC.dispatch(EntityAction::getType, EntityActionType::packetCodec);

	@Override
	EntityActionType<?> getType();

	@Override
	default Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(ContextParameters.THIS_ENTITY);
	}

	@Override
	default String asDisplayString() {
		return "Entity action with type \"" + RegistryUtil.getId(NeoApoliRegistries.ACTION_TYPE, this.getType()) + "\"";
	}

}
