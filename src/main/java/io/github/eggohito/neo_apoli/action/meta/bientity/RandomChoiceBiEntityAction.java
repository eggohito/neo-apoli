package io.github.eggohito.neo_apoli.action.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.meta.RandomChoiceMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.collection.WeightedList;

public record RandomChoiceBiEntityAction(WeightedList<BiEntityAction> actions) implements BiEntityAction, RandomChoiceMetaAction<BiEntityAction, BiEntityActionType<?>> {

	public static final MapCodec<RandomChoiceBiEntityAction> CODEC = NeoApoliCodecs.lazyMap(RandomChoiceBiEntityAction.class.getSimpleName(), () -> RandomChoiceMetaAction.codec(BiEntityAction.CODEC, RandomChoiceBiEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, RandomChoiceBiEntityAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(RandomChoiceBiEntityAction.class.getSimpleName(), () -> RandomChoiceMetaAction.packetCodec(BiEntityAction.PACKET_CODEC, RandomChoiceBiEntityAction::new));

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.RANDOM_CHOICE;
	}

}
