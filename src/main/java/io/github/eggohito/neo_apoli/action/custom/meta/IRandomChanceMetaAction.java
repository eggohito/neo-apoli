package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

import java.util.Optional;

public interface IRandomChanceMetaAction<A extends Action> extends MetaAction {

	A successAction();

	Optional<A> failAction();

	NumberProvider chance();

	@Override
	default void execute(Context context) {

		Context chanceContext = context.forChild(".chance");
		float chance = Mth.clamp(chance().nextFloat(chanceContext), 0.0f, 1.0f);

		if (!chanceContext.hasErrors()) {

			if (context.getLevel().getRandom().nextFloat() < chance) {
				successAction().execute(context.forChild(".success_action"));
			}

			else {
				failAction().ifPresent(elseAction -> elseAction.execute(context.forChild(".fail_action")));
			}

		}

	}

	@Override
	default void validate(Context.Validator validator) {

		MetaAction.super.validate(validator);

		successAction().validate(validator.forChild(".success_action"));
		failAction().ifPresent(failAction -> failAction.validate(validator.forChild(".fail_action")));

		chance().validate(validator.forChild(".chance"));

	}

	static <A extends Action, M extends IRandomChanceMetaAction<A>> MapCodec<M> createCodec(Codec<A> actionCodec, Function3<A, Optional<A>, NumberProvider, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			actionCodec.fieldOf("success_action").forGetter(IRandomChanceMetaAction::successAction),
			actionCodec.optionalFieldOf("fail_action").forGetter(IRandomChanceMetaAction::failAction),
			NumberProvider.CODEC.fieldOf("chance").forGetter(IRandomChanceMetaAction::chance)
		).apply(instance, constructor));
	}

	static <A extends Action, M extends IRandomChanceMetaAction<A>> StreamCodec<RegistryFriendlyByteBuf, M> createStreamCodec(StreamCodec<RegistryFriendlyByteBuf, A> actionCodec, Function3<A, Optional<A>, NumberProvider, M> constructor) {
		return StreamCodec.composite(
			actionCodec, IRandomChanceMetaAction::successAction,
			ByteBufCodecs.optional(actionCodec), IRandomChanceMetaAction::failAction,
			NumberProvider.STREAM_CODEC, IRandomChanceMetaAction::chance,
			constructor
		);
	}

}
