package io.github.eggohito.neo_apoli.action.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.meta.RandomChoiceMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.collection.WeightedList;

@EqualsAndHashCode
@Data
public final class RandomChoiceBiEntityAction extends BiEntityAction implements RandomChoiceMetaAction<BiEntityAction> {

	public static final MapCodec<RandomChoiceBiEntityAction> CODEC = MapCodecUtil.lazy(RandomChoiceBiEntityAction.class.getSimpleName(), () -> RandomChoiceMetaAction.codec(BiEntityAction.CODEC, RandomChoiceBiEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, RandomChoiceBiEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(RandomChoiceBiEntityAction.class.getSimpleName(), () -> RandomChoiceMetaAction.packetCodec(BiEntityAction.PACKET_CODEC, RandomChoiceBiEntityAction::new));

	private final WeightedList<BiEntityAction> actions;

	public RandomChoiceBiEntityAction(WeightedList<BiEntityAction> actions) {
		this.actions = actions;
	}

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.RANDOM_CHOICE;
	}

	@Override
	public void impl(Context context) {
		RandomChoiceMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		RandomChoiceMetaAction.super.validate(reporter);
	}

}
