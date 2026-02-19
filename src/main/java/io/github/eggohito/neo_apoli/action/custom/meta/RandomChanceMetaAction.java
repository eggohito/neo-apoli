package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

import java.util.Optional;

public interface RandomChanceMetaAction<A extends Action> extends Action {

	A successAction();

	Optional<A> failAction();

	NumberProvider chance();

	@Override
	default void execute(Context context) {

		if (context.level().getRandom().nextFloat() < Mth.clamp(chance().nextFloat(context.forChild(".chance")), 0.0F, 1.0F)) {
			successAction().execute(context.forChild(".success_action"));
		}

		else {
			failAction().ifPresent(failAction -> failAction.execute(context.forChild(".fail_action")));
		}

	}

	@Override
	default void validate(Context.Validator validator) {

		Action.super.validate(validator);

		successAction().validate(validator.forChild(".success_action"));
		failAction().ifPresent(failAction -> failAction.validate(validator.forChild(".fail_action")));

		chance().validate(validator.forChild(".chance"));

	}

	static <A extends Action, M extends RandomChanceMetaAction<A>> MapCodec<M> mapCodec(Codec<A> actionCodec, Function3<A, Optional<A>, NumberProvider, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			actionCodec.fieldOf("success_action").forGetter(RandomChanceMetaAction::successAction),
			actionCodec.optionalFieldOf("fail_action").forGetter(RandomChanceMetaAction::failAction),
			NumberProvider.CODEC.fieldOf("chance").forGetter(RandomChanceMetaAction::chance)
		).apply(instance, constructor));
	}

	static <A extends Action, M extends RandomChanceMetaAction<A>> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(StreamCodec<RegistryFriendlyByteBuf, A> actionCodec, Function3<A, Optional<A>, NumberProvider, M> constructor) {
		return StreamCodec.composite(
			actionCodec, RandomChanceMetaAction::successAction,
			ByteBufCodecs.optional(actionCodec), RandomChanceMetaAction::failAction,
			NumberProvider.STREAM_CODEC, RandomChanceMetaAction::chance,
			constructor
		);
	}

}
