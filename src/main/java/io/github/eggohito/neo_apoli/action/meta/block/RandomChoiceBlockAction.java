package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.meta.RandomChoiceMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.collection.WeightedList;

@EqualsAndHashCode
@Data
public final class RandomChoiceBlockAction extends BlockAction implements RandomChoiceMetaAction<BlockAction> {

	public static final MapCodec<RandomChoiceBlockAction> CODEC = NeoApoliMapCodecs.lazy(RandomChoiceBlockAction.class.getSimpleName(), () -> RandomChoiceMetaAction.codec(BlockAction.CODEC, RandomChoiceBlockAction::new));
	public static final PacketCodec<RegistryByteBuf, RandomChoiceBlockAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(RandomChoiceBlockAction.class.getSimpleName(), () -> RandomChoiceMetaAction.packetCodec(BlockAction.PACKET_CODEC, RandomChoiceBlockAction::new));

	private final WeightedList<BlockAction> actions;

	public RandomChoiceBlockAction(WeightedList<BlockAction> actions) {
		this.actions = actions;
	}

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.RANDOM_CHOICE;
	}

	@Override
	public void impl(ServerContext context) {
		RandomChoiceMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		RandomChoiceMetaAction.super.validate(reporter);
	}

}
