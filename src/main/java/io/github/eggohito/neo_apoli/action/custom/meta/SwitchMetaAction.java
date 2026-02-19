package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.ListIterator;
import java.util.function.BiFunction;

public interface SwitchMetaAction<C extends Condition, A extends Action> extends Action {

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

			aCase.value().execute(caseContext.forChild(".action"));
			return;

		}

		defaultAction().execute(context.forChild(".default"));

	}

	@Override
	default void validate(Context.Validator validator) {

		Action.super.validate(validator);

		MiscUtil.iterateList(
			cases(),
			(index, aCase) -> {

				Context.Validator caseValidator = validator.forChild(".cases[" + index + "]");

				aCase.condition().validate(caseValidator.forChild(".condition"));
				aCase.value().validate(caseValidator.forChild(".action"));

			}
		);

		defaultAction().validate(validator.forChild(".default"));

	}

	static <C extends Condition, A extends Action, M extends SwitchMetaAction<C, A>> MapCodec<M> mapCodec(Codec<C> conditionCodec, Codec<A> actionCodec, BiFunction<List<Case<C, A>>, A, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Case.codec(conditionCodec.fieldOf("condition"), actionCodec.fieldOf("action")).listOf().fieldOf("cases").forGetter(SwitchMetaAction::cases),
			actionCodec.fieldOf("default").forGetter(SwitchMetaAction::defaultAction)
		).apply(instance, constructor));
	}

	static <C extends Condition, A extends Action, M extends SwitchMetaAction<C, A>> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(StreamCodec<RegistryFriendlyByteBuf, C> conditionCodec, StreamCodec<RegistryFriendlyByteBuf, A> actionCodec, BiFunction<List<Case<C, A>>, A, M> constructor) {
		return StreamCodec.composite(
			Case.streamCodec(conditionCodec, actionCodec).apply(ByteBufCodecs.list()), SwitchMetaAction::cases,
			actionCodec, SwitchMetaAction::defaultAction,
			constructor
		);
	}

}
