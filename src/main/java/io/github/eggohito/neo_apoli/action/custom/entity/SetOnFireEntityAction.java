package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record SetOnFireEntityAction(NumberProvider ticks) implements EntityAction {

	public static final MapCodec<SetOnFireEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(NumberProvider.CODEC.fieldOf("ticks").forGetter(SetOnFireEntityAction::ticks))
		.apply(instance, SetOnFireEntityAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, SetOnFireEntityAction> STREAM_CODEC = StreamCodec.composite(
		NumberProvider.STREAM_CODEC, SetOnFireEntityAction::ticks,
		SetOnFireEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.SET_ON_FIRE;
	}

	@Override
	public void execute(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		Context ticksContext = context.makeChild(".ticks");
		int ticks = ticks().nextInt(ticksContext);

		if (!ticksContext.hasErrors() && ticks > 0) {
			context.required(NeoApoliContextKeys.THIS_ENTITY).igniteForTicks(ticks);
		}

	}

	@Override
	public void validate(ProblemReporter reporter) {
		EntityAction.super.validate(reporter);
		ticks().validate(reporter.forChild(".ticks"));
	}

}
