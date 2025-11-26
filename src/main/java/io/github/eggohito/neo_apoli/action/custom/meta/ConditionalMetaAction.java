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

public interface ConditionalMetaAction<C extends Condition, A extends Action> extends MetaAction {

	C condition();

	A ifAction();

	Optional<A> elseAction();

	@Override
	default void execute(Context context) {

		Context conditionContext = context.makeChild(".condition");
		boolean shouldExecute = condition().test(conditionContext);

		if (!conditionContext.hasErrors()) {

			if (shouldExecute) {
				ifAction().execute(context.makeChild(".if_action"));
			}

			else {
				elseAction().ifPresent(elseAction -> elseAction.execute(context.makeChild(".else_action")));
			}

		}

	}

	@Override
	default void validate(ProblemReporter reporter) {

		MetaAction.super.validate(reporter);
		condition().validate(reporter.forChild(".condition"));

		ifAction().validate(reporter.forChild(".if_action"));
		elseAction().ifPresent(elseAction -> elseAction.validate(reporter.forChild(".else_action")));

	}

	static <C extends Condition, A extends Action, M extends ConditionalMetaAction<C, A>> MapCodec<M> createCodec(Codec<C> conditionCodec, Codec<A> actionCodec, Function3<C, A, Optional<A>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			conditionCodec.fieldOf("condition").forGetter(ConditionalMetaAction::condition),
			actionCodec.fieldOf("if_action").forGetter(ConditionalMetaAction::ifAction),
			actionCodec.optionalFieldOf("else_action").forGetter(ConditionalMetaAction::elseAction)
		).apply(instance, constructor));
	}

	static <C extends Condition, A extends Action, M extends ConditionalMetaAction<C, A>> StreamCodec<RegistryFriendlyByteBuf, M> createStreamCodec(StreamCodec<RegistryFriendlyByteBuf, C> conditionCodec, StreamCodec<RegistryFriendlyByteBuf, A> actionCodec, Function3<C, A, Optional<A>, M> constructor) {
		return StreamCodec.composite(
			conditionCodec, ConditionalMetaAction::condition,
			actionCodec, ConditionalMetaAction::ifAction,
			ByteBufCodecs.optional(actionCodec), ConditionalMetaAction::elseAction,
			constructor
		);
	}

}
