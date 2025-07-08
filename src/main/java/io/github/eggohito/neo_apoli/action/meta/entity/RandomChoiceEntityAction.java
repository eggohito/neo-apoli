package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.RandomChoiceMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
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
public final class RandomChoiceEntityAction extends EntityAction implements RandomChoiceMetaAction<EntityAction> {

	public static final MapCodec<RandomChoiceEntityAction> CODEC = MapCodecUtil.lazy(RandomChoiceEntityAction.class.getSimpleName(), () -> RandomChoiceMetaAction.codec(EntityAction.CODEC, RandomChoiceEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, RandomChoiceEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(RandomChoiceEntityAction.class.getSimpleName(), () -> RandomChoiceMetaAction.packetCodec(EntityAction.PACKET_CODEC, RandomChoiceEntityAction::new));

	private final WeightedList<EntityAction> actions;

	public RandomChoiceEntityAction(WeightedList<EntityAction> actions) {
		this.actions = actions;
	}

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.RANDOM_CHOICE;
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
