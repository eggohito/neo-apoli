package io.github.eggohito.neo_apoli.action.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.context.ActionContext;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.context.ConditionContext;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.List;
import java.util.function.Function;

public interface IfElseListMetaAction<AX extends ActionContext<CX>, CX extends ConditionContext, AA extends Action<AX, AT>, CC extends Condition<CX, CT>, AT extends ActionType<?>, CT extends ConditionType<?>> extends Action<AX, AT> {

	List<Entry<CC, AA>> entries();

	@Override
	default void execute(ErrorReporter reporter, AX context) {

		CX convertedContext = context.convert();

		for (Entry<CC, AA> entry : entries()) {

			if (entry.condition().test(reporter, convertedContext)) {
				entry.action().execute(reporter, context);
				break;
			}

		}

	}

	@Override
	default void validate(ErrorReporter reporter) {

		for (int i = 0; i < entries().size(); i++) {

			Entry<CC, AA> entry = entries().get(i);
			ErrorReporter entryReporter = reporter.makeChild("actions[" + i + "]");

			entry.condition().validate(entryReporter.makeChild("condition"));
			entry.action().validate(entryReporter.makeChild("action"));

		}

	}

	static <A extends Action<?, ?>, C extends Condition<?, ?>, M extends IfElseListMetaAction<?, ?, A, C, ?, ?>> MapCodec<M> createCodec(Codec<C> conditionCodec, Codec<A> actionCodec, Function<List<Entry<C, A>>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Entry.createCodec(conditionCodec, actionCodec).listOf().fieldOf("actions").forGetter(IfElseListMetaAction::entries)
		).apply(instance, constructor));
	}

	static <B extends ByteBuf, A extends Action<?, ?>, C extends Condition<?, ?>, M extends IfElseListMetaAction<?, ?, A, C, ?, ?>> PacketCodec<B, M> createPacketCodec(PacketCodec<B, C> conditionCodec, PacketCodec<B, A> actionCodec, Function<List<Entry<C, A>>, M> constructor) {
		return PacketCodecs.collection(ObjectArrayList::new, Entry.createPacketCodec(conditionCodec, actionCodec)).xmap(constructor, ifElseListMetaAction -> new ObjectArrayList<>(ifElseListMetaAction.entries()));
	}

	record Entry<C extends Condition<?, ?>, A extends Action<?, ?>>(C condition, A action) {

		public static <C extends Condition<?, ?>, A extends Action<?, ?>> MapCodec<Entry<C, A>> createMapCodec(Codec<C> conditionCodec, Codec<A> actionCodec) {
			return RecordCodecBuilder.mapCodec(instance -> instance.group(
				conditionCodec.fieldOf("condition").forGetter(Entry::condition),
				actionCodec.fieldOf("action").forGetter(Entry::action)
			).apply(instance, Entry::new));
		}

		public static <C extends Condition<?, ?>, A extends Action<?, ?>> Codec<Entry<C, A>> createCodec(Codec<C> conditionCodec, Codec<A> actionCodec) {
			return createMapCodec(conditionCodec, actionCodec).codec();
		}

		public static <B extends ByteBuf, C extends Condition<?, ?>, A extends Action<?, ?>> PacketCodec<B, Entry<C, A>> createPacketCodec(PacketCodec<B, C> conditionCodec, PacketCodec<B, A> actionCodec) {
			return PacketCodec.tuple(
				conditionCodec, Entry::condition,
				actionCodec, Entry::action,
				Entry::new
			);
		}

	}

}
