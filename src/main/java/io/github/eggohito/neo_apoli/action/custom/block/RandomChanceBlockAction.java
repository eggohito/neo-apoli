package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.RandomChanceMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record RandomChanceBlockAction(BlockAction successAction, Optional<BlockAction> failAction, NumberProvider chance) implements BlockAction, RandomChanceMetaAction<BlockAction> {

	public static final MapCodec<RandomChanceBlockAction> CODEC = MapCodecUtil.lazy(RandomChanceBlockAction.class.getSimpleName(), () -> RandomChanceMetaAction.codec(BlockAction.CODEC, RandomChanceBlockAction::new));
	public static final PacketCodec<RegistryByteBuf, RandomChanceBlockAction> PACKET_CODEC = PacketCodecUtil.lazy(RandomChanceBlockAction.class.getSimpleName(), () -> RandomChanceMetaAction.packetCodec(BlockAction.PACKET_CODEC, RandomChanceBlockAction::new));

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.RANDOM_CHANCE;
	}

	@Override
	public void execute(Context context) {
		RandomChanceMetaAction.super.execute(context);
	}

	@Override
	public void serverExecute(ServerContext context) {
		RandomChanceMetaAction.super.execute(context);
	}

	@Override
	public String asDisplayString() {
		return BlockAction.super.asDisplayString();
	}

}
