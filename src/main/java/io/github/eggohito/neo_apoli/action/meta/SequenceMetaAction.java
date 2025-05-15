package io.github.eggohito.neo_apoli.action.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.List;
import java.util.ListIterator;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface SequenceMetaAction<A extends Action<T>, T extends ActionType<?>> extends Action<T> {

	List<A> actions();

	@Override
	default void execute(Context context) {
		this.iterate((index, action) -> action.execute(context.makeChild("actions[" + index + "]")));
	}

	@Override
	default void validate(ErrorReporter reporter) {
		this.iterate((index, action) -> action.validate(reporter.makeChild("actions[" + index + "]")));
	}

	default void iterate(BiConsumer<Integer, A> processor) {

		ListIterator<A> actionIterator = actions().listIterator();

		while (actionIterator.hasNext()) {
			processor.accept(actionIterator.nextIndex(), actionIterator.next());
		}

	}

	static <A extends Action<?>, M extends SequenceMetaAction<A, ?>> MapCodec<M> codec(Codec<A> elementCodec, Function<List<A>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			elementCodec.listOf().fieldOf("actions").forGetter(SequenceMetaAction::actions)
		).apply(instance, constructor));
	}

	static <B extends ByteBuf, A extends Action<?>, M extends SequenceMetaAction<A, ?>> PacketCodec<B, M> packetCodec(PacketCodec<B, A> elementCodec, Function<List<A>, M> constructor) {
		return PacketCodecs.collection(ObjectArrayList::new, elementCodec).xmap(constructor, sequenceMetaAction -> new ObjectArrayList<>(sequenceMetaAction.actions()));
	}

}
