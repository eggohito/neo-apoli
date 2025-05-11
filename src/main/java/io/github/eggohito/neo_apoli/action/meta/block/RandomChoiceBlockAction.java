package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.meta.RandomChoiceMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.collection.WeightedList;

public record RandomChoiceBlockAction(WeightedList<BlockAction> actions) implements BlockAction, RandomChoiceMetaAction<BlockAction, BlockActionType<?>> {

	public static final MapCodec<RandomChoiceBlockAction> CODEC = NeoApoliCodecs.lazyMap("RandomChoiceBlockAction", () -> RandomChoiceMetaAction.createCodec(BlockAction.CODEC, RandomChoiceBlockAction::new));
	public static final PacketCodec<RegistryByteBuf, RandomChoiceBlockAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy("RandomChoiceBlockAction", () -> RandomChoiceMetaAction.createPacketCodec(BlockAction.PACKET_CODEC, RandomChoiceBlockAction::new));

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.RANDOM_CHOICE;
	}

}
