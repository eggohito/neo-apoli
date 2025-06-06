package io.github.eggohito.neo_apoli.action.meta;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.Optional;

public interface IfElseMetaAction<A extends Action<?>, C extends Condition<?>, AT extends ActionType<?>, CT extends ConditionType<?>> extends Action<AT> {

	C condition();

	A ifAction();

	Optional<A> elseAction();

	@Override
	default void execute(Context context) {

		Context conditionContext = context.makeChild("condition");
		boolean shouldExecute = this.condition().test(conditionContext);

		if (!conditionContext.hasErrors()) {

			if (shouldExecute) {
				this.ifAction().execute(context.makeChild("if_action"));
			}

			else {
				this.elseAction().ifPresent(elseAction -> elseAction.execute(context.makeChild("else_action")));
			}

		}

	}

	@Override
	default void validate(ErrorReporter reporter) {

		Action.super.validate(reporter);

		condition().validate(reporter.makeChild("condition"));
		ifAction().validate(reporter.makeChild("if_action"));

		elseAction().ifPresent(elseAction -> elseAction.validate(reporter.makeChild("else_action")));

	}

	static <A extends Action<?>, C extends Condition<?>, M extends IfElseMetaAction<A, C, ?, ?>> MapCodec<M> codec(Codec<C> conditionCodec, Codec<A> actionCodec, Function3<C, A, Optional<A>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			conditionCodec.fieldOf("condition").forGetter(IfElseMetaAction::condition),
			actionCodec.fieldOf("if_action").forGetter(IfElseMetaAction::ifAction),
			actionCodec.optionalFieldOf("else_action").forGetter(IfElseMetaAction::elseAction)
		).apply(instance, constructor));
	}

	static <B extends ByteBuf, A extends Action<?>, C extends Condition<?>, M extends IfElseMetaAction<A, C, ?, ?>> PacketCodec<B, M> packetCodec(PacketCodec<B, C> conditionCodec, PacketCodec<B, A> actionCodec, Function3<C, A, Optional<A>, M> constructor) {
		return PacketCodec.tuple(
			conditionCodec, IfElseMetaAction::condition,
			actionCodec, IfElseMetaAction::ifAction,
			PacketCodecs.optional(actionCodec), IfElseMetaAction::elseAction,
			constructor
		);
	}

}
