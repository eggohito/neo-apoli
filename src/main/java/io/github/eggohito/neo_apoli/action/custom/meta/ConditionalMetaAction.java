package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

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
	default void validate(ErrorReporter reporter) {

		MetaAction.super.validate(reporter);
		condition().validate(reporter.makeChild(".condition"));

		ifAction().validate(reporter.makeChild(".if_action"));
		elseAction().ifPresent(elseAction -> elseAction.validate(reporter.makeChild(".else_action")));

	}

	static <C extends Condition, A extends Action, M extends ConditionalMetaAction<C, A>> MapCodec<M> codec(Codec<C> conditionCodec, Codec<A> actionCodec, Function3<C, A, Optional<A>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			conditionCodec.fieldOf("condition").forGetter(ConditionalMetaAction::condition),
			actionCodec.fieldOf("if_action").forGetter(ConditionalMetaAction::ifAction),
			actionCodec.optionalFieldOf("else_action").forGetter(ConditionalMetaAction::elseAction)
		).apply(instance, constructor));
	}

	static <C extends Condition, A extends Action, M extends ConditionalMetaAction<C, A>> PacketCodec<RegistryByteBuf, M> packetCodec(PacketCodec<RegistryByteBuf, C> conditionCodec, PacketCodec<RegistryByteBuf, A> actionCodec, Function3<C, A, Optional<A>, M> constructor) {
		return PacketCodec.tuple(
			conditionCodec, ConditionalMetaAction::condition,
			actionCodec, ConditionalMetaAction::ifAction,
			PacketCodecs.optional(actionCodec), ConditionalMetaAction::elseAction,
			constructor
		);
	}

}
