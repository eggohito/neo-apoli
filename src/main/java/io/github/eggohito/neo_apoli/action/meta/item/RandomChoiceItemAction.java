package io.github.eggohito.neo_apoli.action.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.meta.RandomChoiceMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.collection.WeightedList;

@EqualsAndHashCode
@Data
public final class RandomChoiceItemAction extends ItemAction implements RandomChoiceMetaAction<ItemAction> {

	public static final MapCodec<RandomChoiceItemAction> CODEC = MapCodecUtil.lazy(RandomChoiceItemAction.class.getSimpleName(), () -> RandomChoiceMetaAction.codec(ItemAction.CODEC, RandomChoiceItemAction::new));
	public static final PacketCodec<RegistryByteBuf, RandomChoiceItemAction> PACKET_CODEC = PacketCodecUtil.lazy(RandomChoiceItemAction.class.getSimpleName(), () -> RandomChoiceMetaAction.packetCodec(ItemAction.PACKET_CODEC, RandomChoiceItemAction::new));

	private final WeightedList<ItemAction> actions;

	public RandomChoiceItemAction(WeightedList<ItemAction> actions) {
		this.actions = actions;
	}

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.RANDOM_CHOICE;
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
