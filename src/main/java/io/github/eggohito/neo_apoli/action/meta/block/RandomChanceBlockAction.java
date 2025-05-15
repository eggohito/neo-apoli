package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.meta.RandomChanceMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record RandomChanceBlockAction(BlockAction successAction, Optional<BlockAction> failAction, float chance) implements BlockAction, RandomChanceMetaAction<BlockAction, BlockActionType<?>> {

	public static final MapCodec<RandomChanceBlockAction> CODEC = NeoApoliCodecs.lazyMap("RandomChanceBlockAction", () -> RandomChanceMetaAction.codec(BlockAction.CODEC, RandomChanceBlockAction::new));
	public static final PacketCodec<RegistryByteBuf, RandomChanceBlockAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy("RandomChanceBlockAction", () -> RandomChanceMetaAction.packetCodec(BlockAction.PACKET_CODEC, RandomChanceBlockAction::new));

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.RANDOM_CHANCE;
	}

}
