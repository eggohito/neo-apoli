package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.meta.RandomChanceMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

@EqualsAndHashCode
@Data
public final class RandomChanceBlockAction extends BlockAction implements RandomChanceMetaAction<BlockAction> {

	public static final MapCodec<RandomChanceBlockAction> CODEC = NeoApoliMapCodecs.lazy(RandomChanceBlockAction.class.getSimpleName(), () -> RandomChanceMetaAction.codec(BlockAction.CODEC, RandomChanceBlockAction::new));
	public static final PacketCodec<RegistryByteBuf, RandomChanceBlockAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(RandomChanceBlockAction.class.getSimpleName(), () -> RandomChanceMetaAction.packetCodec(BlockAction.PACKET_CODEC, RandomChanceBlockAction::new));

	private final BlockAction successAction;
	private final Optional<BlockAction> failAction;

	private final float chance;

	public RandomChanceBlockAction(BlockAction successAction, Optional<BlockAction> failAction, float chance) {
		this.successAction = successAction;
		this.failAction = failAction;
		this.chance = chance;
	}

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.RANDOM_CHANCE;
	}

	@Override
	public void impl(ServerContext context) {
		RandomChanceMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		RandomChanceMetaAction.super.validate(reporter);
	}

}
