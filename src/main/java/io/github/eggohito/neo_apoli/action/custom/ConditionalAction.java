package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConditionalAction(Condition condition, Action ifAction, Action elseAction) implements Action {

	public static final MapCodec<ConditionalAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Condition.CODEC.fieldOf("condition").forGetter(ConditionalAction::condition),
		Action.CODEC.fieldOf("if_action").forGetter(ConditionalAction::ifAction),
		Action.CODEC.optionalFieldOf("else_action", NothingAction.INSTANCE).forGetter(ConditionalAction::elseAction)
	).apply(instance, ConditionalAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalAction> STREAM_CODEC = StreamCodec.composite(
		Condition.STREAM_CODEC, ConditionalAction::condition,
		Action.STREAM_CODEC, ConditionalAction::ifAction,
		Action.STREAM_CODEC, ConditionalAction::elseAction,
		ConditionalAction::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliActionTypes.CONDITIONAL;
	}

	@Override
	public void execute(Context context) {

		if (condition().test(context.forChild(".condition"))) {
			ifAction().execute(context.forChild(".if_action"));
		}

		else {
			elseAction().execute(context.forChild(".else_action"));
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		Action.super.validate(validator);
		condition().validate(validator.forChild(".condition"));
		ifAction().validate(validator.forChild(".if_action"));
		elseAction().validate(validator.forChild(".else_action"));
	}

}
