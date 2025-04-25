package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.RandomChoiceMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.collection.WeightedList;

public record RandomChoiceEntityAction(WeightedList<EntityAction> actions) implements EntityAction, RandomChoiceMetaAction<EntityAction, EntityActionType<?>> {

	public static final MapCodec<RandomChoiceEntityAction> CODEC = RandomChoiceMetaAction.createCodec(EntityAction.CODEC, RandomChoiceEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, RandomChoiceEntityAction> PACKET_CODEC = RandomChoiceMetaAction.createPacketCodec(EntityAction.PACKET_CODEC, RandomChoiceEntityAction::new);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.RANDOM_CHOICE;
	}

}
