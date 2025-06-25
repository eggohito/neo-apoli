package io.github.eggohito.neo_apoli.action.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.List;
import java.util.ListIterator;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public interface IfElseListMetaAction<A extends Action, C extends Condition> {

	List<Entry<C, A>> entries();

	default void impl(Context context) {

		MutableBoolean result = new MutableBoolean();
		this.iterate((index, entry) -> {

			Context subContext = context.makeChild(".actions[" + index + "]");
			boolean shouldExecute = entry.condition().test(subContext.makeChild(".condition"));

			if (shouldExecute) {
				entry.action().execute(subContext.makeChild(".action"));
			}

			result.setValue(shouldExecute);

		}, () -> !result.getValue());

	}

	default void validate(ContextAware.ErrorReporter reporter) {
		this.iterate((index, entry) -> {

			ContextAware.ErrorReporter subReporter = reporter.makeChild(".actions[" + index + "]");

			entry.condition().validate(subReporter.makeChild(".condition"));
			entry.action().validate(subReporter.makeChild(".action"));

		});
	}

	default void iterate(BiConsumer<Integer, Entry<C, A>> processor, BooleanSupplier continueCondition) {

		ListIterator<Entry<C, A>> entryIterator = entries().listIterator();
		boolean init = false;

		while (entryIterator.hasNext()) {

			if (init && !continueCondition.getAsBoolean()) {
				break;
			}

			processor.accept(entryIterator.nextIndex(), entryIterator.next());
			init = true;

		}

	}

	default void iterate(BiConsumer<Integer, Entry<C, A>> processor) {
		this.iterate(processor, () -> true);
	}

	static <A extends Action, C extends Condition, M extends IfElseListMetaAction<A, C>> MapCodec<M> codec(Codec<C> conditionCodec, Codec<A> actionCodec, Function<List<Entry<C, A>>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Entry.codec(conditionCodec, actionCodec).listOf().fieldOf("actions").forGetter(IfElseListMetaAction::entries)
		).apply(instance, constructor));
	}

	static <B extends ByteBuf, A extends Action, C extends Condition, M extends IfElseListMetaAction<A, C>> PacketCodec<B, M> packetCodec(PacketCodec<B, C> conditionCodec, PacketCodec<B, A> actionCodec, Function<List<Entry<C, A>>, M> constructor) {
		return PacketCodecs.collection(ObjectArrayList::new, Entry.packetCodec(conditionCodec, actionCodec)).xmap(constructor, ifElseListMetaAction -> new ObjectArrayList<>(ifElseListMetaAction.entries()));
	}

	record Entry<C extends Condition, A extends Action>(C condition, A action) {

		public static <C extends Condition, A extends Action> MapCodec<Entry<C, A>> mapCodec(Codec<C> conditionCodec, Codec<A> actionCodec) {
			return RecordCodecBuilder.mapCodec(instance -> instance.group(
				conditionCodec.fieldOf("condition").forGetter(Entry::condition),
				actionCodec.fieldOf("action").forGetter(Entry::action)
			).apply(instance, Entry::new));
		}

		public static <C extends Condition, A extends Action> Codec<Entry<C, A>> codec(Codec<C> conditionCodec, Codec<A> actionCodec) {
			return mapCodec(conditionCodec, actionCodec).codec();
		}

		public static <B extends ByteBuf, C extends Condition, A extends Action> PacketCodec<B, Entry<C, A>> packetCodec(PacketCodec<B, C> conditionCodec, PacketCodec<B, A> actionCodec) {
			return PacketCodec.tuple(
				conditionCodec, Entry::condition,
				actionCodec, Entry::action,
				Entry::new
			);
		}

	}

}
