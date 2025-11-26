package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.ListIterator;
import java.util.function.BiFunction;

public interface ChoiceMetaAction<C extends Condition, A extends Action> extends MetaAction {

	List<Case<C, A>> cases();

	A defaultAction();

	@Override
	default void execute(Context context) {

		ListIterator<Case<C, A>> listIterator = cases().listIterator();
		boolean executed = false;

		while (!executed && listIterator.hasNext()) {

			int index = listIterator.nextIndex();
			Case<C, A> aCase = listIterator.next();

			Context caseContext = context.makeChild(".cases[" + index + "]");

			boolean shouldExecute = aCase.condition().test(caseContext.makeChild(".condition"));
			executed = !caseContext.hasErrors() && shouldExecute;

			if (executed) {
				aCase.action().execute(caseContext.makeChild(".action"));
			}

		}

		if (!executed) {
			defaultAction().execute(context.makeChild(".default"));
		}

	}

	@Override
	default void validate(ProblemReporter reporter) {

		MetaAction.super.validate(reporter);
		ListIterator<Case<C, A>> listIterator = cases().listIterator();

		while (listIterator.hasNext()) {

			int index = listIterator.nextIndex();
			Case<C, A> aCase = listIterator.next();

			aCase.validate(reporter.forChild(".cases[" + index + "]"));

		}

		defaultAction().validate(reporter.forChild(".default"));

	}

	static <C extends Condition, A extends Action, M extends ChoiceMetaAction<C, A>> MapCodec<M> createCodec(Codec<C> conditionCodec, Codec<A> actionCodec, BiFunction<List<Case<C, A>>, A, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Case.createCodec(conditionCodec, actionCodec).listOf().fieldOf("cases").forGetter(ChoiceMetaAction::cases),
			actionCodec.fieldOf("default").forGetter(ChoiceMetaAction::defaultAction)
		).apply(instance, constructor));
	}

	static <C extends Condition, A extends Action, M extends ChoiceMetaAction<C, A>> StreamCodec<RegistryFriendlyByteBuf, M> createStreamCodec(StreamCodec<RegistryFriendlyByteBuf, C> conditionCodec, StreamCodec<RegistryFriendlyByteBuf, A> actionCodec, BiFunction<List<Case<C, A>>, A, M> constructor) {
		return StreamCodec.composite(
			ByteBufCodecs.collection(ObjectArrayList::new, Case.createStreamCodec(conditionCodec, actionCodec)), ChoiceMetaAction::cases,
			actionCodec, ChoiceMetaAction::defaultAction,
			constructor
		);
	}

	record Case<C extends Condition, A extends Action>(C condition, A action) implements ContextAware {

		@Override
		public void validate(ProblemReporter reporter) {

			ContextAware.super.validate(reporter);

			condition().validate(reporter.forChild(".condition"));
			action().validate(reporter.forChild(".action"));

		}

		public static <C extends Condition, A extends Action> Codec<Case<C, A>> createCodec(Codec<C> conditionCodec, Codec<A> actionCodec) {
			return RecordCodecBuilder.create(instance -> instance.group(
				conditionCodec.fieldOf("condition").forGetter(Case::condition),
				actionCodec.fieldOf("action").forGetter( Case::action)
			).apply(instance, Case::new));
		}

		public static <C extends Condition, A extends Action> StreamCodec<RegistryFriendlyByteBuf, Case<C, A>> createStreamCodec(StreamCodec<RegistryFriendlyByteBuf, C> conditionCodec, StreamCodec<RegistryFriendlyByteBuf, A> actionCodec) {
			return StreamCodec.composite(
				conditionCodec, Case::condition,
				actionCodec, Case::action,
				Case::new
			);
		}

	}

}
