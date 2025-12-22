package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public interface IConditionalMetaAction<C extends Condition, A extends Action> extends MetaAction {

	C condition();

	A ifAction();

	Optional<A> elseAction();

	@Override
	default void execute(Context context) {

		Context conditionContext = context.forChild(".condition");
		boolean shouldExecute = condition().test(conditionContext);

		if (!conditionContext.hasErrors()) {

			if (shouldExecute) {
				ifAction().execute(context.forChild(".if_action"));
			}

			else {
				elseAction().ifPresent(elseAction -> elseAction.execute(context.forChild(".else_action")));
			}

		}

	}

	@Override
	default void validate(Context.Validator validator) {

		MetaAction.super.validate(validator);
		condition().validate(validator.forChild(".condition"));

		ifAction().validate(validator.forChild(".if_action"));
		elseAction().ifPresent(elseAction -> elseAction.validate(validator.forChild(".else_action")));

	}

	static <C extends Condition, A extends Action, M extends IConditionalMetaAction<C, A>> MapCodec<M> createCodec(Codec<C> conditionCodec, Codec<A> actionCodec, Function3<C, A, Optional<A>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			conditionCodec.fieldOf("condition").forGetter(IConditionalMetaAction::condition),
			actionCodec.fieldOf("if_action").forGetter(IConditionalMetaAction::ifAction),
			actionCodec.optionalFieldOf("else_action").forGetter(IConditionalMetaAction::elseAction)
		).apply(instance, constructor));
	}

	static <C extends Condition, A extends Action, M extends IConditionalMetaAction<C, A>> StreamCodec<RegistryFriendlyByteBuf, M> createStreamCodec(StreamCodec<RegistryFriendlyByteBuf, C> conditionCodec, StreamCodec<RegistryFriendlyByteBuf, A> actionCodec, Function3<C, A, Optional<A>, M> constructor) {
		return StreamCodec.composite(
			conditionCodec, IConditionalMetaAction::condition,
			actionCodec, IConditionalMetaAction::ifAction,
			ByteBufCodecs.optional(actionCodec), IConditionalMetaAction::elseAction,
			constructor
		);
	}

}
