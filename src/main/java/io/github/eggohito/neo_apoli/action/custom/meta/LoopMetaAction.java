package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.Optional;

public interface LoopMetaAction<A extends Action> extends MetaAction {

	Optional<A> beforeAction();

	Optional<A> afterAction();

	NumberProvider iterations();

	A action();

	@Override
	default void execute(Context context) {

		beforeAction().ifPresent(beforeAction -> beforeAction.execute(context.makeChild(".before_action")));

		Context iterationsContext = context.makeChild(".iterations");
		int iterations = iterations().nextInt(iterationsContext);

		for (int i = 0; !iterationsContext.hasErrors() && i < iterations; i++) {
			action().execute(context.makeChild(".action"));
		}

		afterAction().ifPresent(afterAction -> afterAction.execute(context.makeChild(".after_action")));

	}

	@Override
	default void validate(ErrorReporter reporter) {

		MetaAction.super.validate(reporter);

		beforeAction().ifPresent(beforeAction -> beforeAction.validate(reporter.makeChild(".before_action")));
		afterAction().ifPresent(afterAction -> afterAction.validate(reporter.makeChild(".after_action")));

		iterations().validate(reporter.makeChild(".iterations"));
		action().validate(reporter.makeChild(".action"));

	}

	static <A extends Action, M extends LoopMetaAction<A>> MapCodec<M> codec(Codec<A> actionCodec, Function4<Optional<A>, Optional<A>, NumberProvider, A, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			actionCodec.optionalFieldOf("before_action").forGetter(LoopMetaAction::beforeAction),
			actionCodec.optionalFieldOf("after_action").forGetter(LoopMetaAction::afterAction),
			NumberProvider.CODEC.fieldOf("iterations").forGetter(LoopMetaAction::iterations),
			actionCodec.fieldOf("action").forGetter(LoopMetaAction::action)
		).apply(instance, constructor));
	}

	static <A extends Action, M extends LoopMetaAction<A>> PacketCodec<RegistryByteBuf, M> packetCodec(PacketCodec<RegistryByteBuf, A> actionCodec, Function4<Optional<A>, Optional<A>, NumberProvider, A, M> constructor) {
		PacketCodec<RegistryByteBuf, Optional<A>> optionalCodec = PacketCodecs.optional(actionCodec);
		return PacketCodec.tuple(
			optionalCodec, LoopMetaAction::beforeAction,
			optionalCodec, LoopMetaAction::afterAction,
			NumberProvider.PACKET_CODEC, LoopMetaAction::iterations,
			actionCodec, LoopMetaAction::action,
			constructor
		);
	}

}
