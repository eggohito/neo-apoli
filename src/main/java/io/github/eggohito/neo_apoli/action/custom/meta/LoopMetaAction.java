package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public interface LoopMetaAction<A extends Action> extends MetaAction {

	Optional<A> beforeAction();

	Optional<A> afterAction();

	NumberProvider iterations();

	A action();

	@Override
	default void execute(Context context) {

		beforeAction().ifPresent(beforeAction -> beforeAction.execute(context.forChild(".before_action")));

		Context iterationsContext = context.forChild(".iterations");
		int iterations = iterations().nextInt(iterationsContext);

		for (int i = 0; !iterationsContext.hasErrors() && i < iterations; i++) {
			action().execute(context.forChild(".action"));
		}

		afterAction().ifPresent(afterAction -> afterAction.execute(context.forChild(".after_action")));

	}

	@Override
	default void validate(Context.Validator validator) {

		MetaAction.super.validate(validator);

		beforeAction().ifPresent(beforeAction -> beforeAction.validate(validator.forChild(".before_action")));
		afterAction().ifPresent(afterAction -> afterAction.validate(validator.forChild(".after_action")));

		iterations().validate(validator.forChild(".iterations"));
		action().validate(validator.forChild(".action"));

	}

	static <A extends Action, M extends LoopMetaAction<A>> MapCodec<M> createCodec(Codec<A> actionCodec, Function4<Optional<A>, Optional<A>, NumberProvider, A, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			actionCodec.optionalFieldOf("before_action").forGetter(LoopMetaAction::beforeAction),
			actionCodec.optionalFieldOf("after_action").forGetter(LoopMetaAction::afterAction),
			NumberProvider.CODEC.fieldOf("iterations").forGetter(LoopMetaAction::iterations),
			actionCodec.fieldOf("action").forGetter(LoopMetaAction::action)
		).apply(instance, constructor));
	}

	static <A extends Action, M extends LoopMetaAction<A>> StreamCodec<RegistryFriendlyByteBuf, M> createStreamCodec(StreamCodec<RegistryFriendlyByteBuf, A> actionCodec, Function4<Optional<A>, Optional<A>, NumberProvider, A, M> constructor) {
		StreamCodec<RegistryFriendlyByteBuf, Optional<A>> optionalCodec = ByteBufCodecs.optional(actionCodec);
		return StreamCodec.composite(
			optionalCodec, LoopMetaAction::beforeAction,
			optionalCodec, LoopMetaAction::afterAction,
			NumberProvider.STREAM_CODEC, LoopMetaAction::iterations,
			actionCodec, LoopMetaAction::action,
			constructor
		);
	}

}
