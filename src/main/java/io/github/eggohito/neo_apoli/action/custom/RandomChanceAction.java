package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record RandomChanceAction(Action successAction, Action failAction, NumberProvider chance) implements Action {

	public static final MapCodec<RandomChanceAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Action.CODEC.fieldOf("success_action").forGetter(RandomChanceAction::successAction),
		Action.CODEC.optionalFieldOf("fail_action", NothingAction.INSTANCE).forGetter(RandomChanceAction::failAction),
		NumberProvider.clamped(0.0, 1.0).fieldOf("chance").forGetter(RandomChanceAction::chance)
	).apply(instance, RandomChanceAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RandomChanceAction> STREAM_CODEC = StreamCodec.composite(
		Action.STREAM_CODEC, RandomChanceAction::successAction,
		Action.STREAM_CODEC, RandomChanceAction::failAction,
		NumberProvider.STREAM_CODEC, RandomChanceAction::chance,
		RandomChanceAction::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliActionTypes.RANDOM_CHANCE;
	}

	@Override
	public void execute(Context context) {

		if (context.level().getRandom().nextFloat() < chance().getFloat(context.forChild(".chance"))) {
			successAction().execute(context.forChild(".success_action"));
		}

		else {
			failAction().execute(context.forChild(".fail_action"));
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		Action.super.validate(validator);
		successAction().validate(validator.forChild(".success_action"));
		failAction().validate(validator.forChild(".fail_action"));
		chance().validate(validator.forChild(".chance"));
	}

}
