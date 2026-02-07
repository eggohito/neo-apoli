package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextUser;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.ListIterator;
import java.util.function.BiFunction;

public interface IChoiceMetaAction<C extends Condition, A extends Action> extends MetaAction {

	List<Case<C, A>> cases();

	A defaultAction();

	@Override
	default void execute(Context context) {

		ListIterator<Case<C, A>> listIterator = cases().listIterator();

		while (listIterator.hasNext()) {

			Context caseContext = context.forChild(".cases[" + listIterator.nextIndex() + "]");
			Case<C, A> aCase = listIterator.next();

			if (!aCase.condition().test(caseContext.forChild(".condition"))) {
				continue;
			}

			aCase.action().execute(caseContext.forChild(".action"));
			return;

		}

		defaultAction().execute(context.forChild(".default"));

	}

	@Override
	default void validate(Context.Validator validator) {

		MetaAction.super.validate(validator);
		ListIterator<Case<C, A>> listIterator = cases().listIterator();

		while (listIterator.hasNext()) {

			int index = listIterator.nextIndex();
			Case<C, A> aCase = listIterator.next();

			aCase.validate(validator.forChild(".cases[" + index + "]"));

		}

		defaultAction().validate(validator.forChild(".default"));

	}

	static <C extends Condition, A extends Action, M extends IChoiceMetaAction<C, A>> MapCodec<M> mapCodec(Codec<C> conditionCodec, Codec<A> actionCodec, BiFunction<List<Case<C, A>>, A, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Case.mapCodec(conditionCodec, actionCodec).listOf().fieldOf("cases").forGetter(IChoiceMetaAction::cases),
			actionCodec.fieldOf("default").forGetter(IChoiceMetaAction::defaultAction)
		).apply(instance, constructor));
	}

	static <C extends Condition, A extends Action, M extends IChoiceMetaAction<C, A>> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(StreamCodec<RegistryFriendlyByteBuf, C> conditionCodec, StreamCodec<RegistryFriendlyByteBuf, A> actionCodec, BiFunction<List<Case<C, A>>, A, M> constructor) {
		return StreamCodec.composite(
			ByteBufCodecs.collection(ObjectArrayList::new, Case.streamCodec(conditionCodec, actionCodec)), IChoiceMetaAction::cases,
			actionCodec, IChoiceMetaAction::defaultAction,
			constructor
		);
	}

	record Case<C extends Condition, A extends Action>(C condition, A action) implements ContextUser {

		@Override
		public void validate(Context.Validator validator) {

			ContextUser.super.validate(validator);

			condition().validate(validator.forChild(".condition"));
			action().validate(validator.forChild(".action"));

		}

		public static <C extends Condition, A extends Action> Codec<Case<C, A>> mapCodec(Codec<C> conditionCodec, Codec<A> actionCodec) {
			return RecordCodecBuilder.create(instance -> instance.group(
				conditionCodec.fieldOf("condition").forGetter(Case::condition),
				actionCodec.fieldOf("action").forGetter( Case::action)
			).apply(instance, Case::new));
		}

		public static <C extends Condition, A extends Action> StreamCodec<RegistryFriendlyByteBuf, Case<C, A>> streamCodec(StreamCodec<RegistryFriendlyByteBuf, C> conditionCodec, StreamCodec<RegistryFriendlyByteBuf, A> actionCodec) {
			return StreamCodec.composite(
				conditionCodec, Case::condition,
				actionCodec, Case::action,
				Case::new
			);
		}

	}

}
