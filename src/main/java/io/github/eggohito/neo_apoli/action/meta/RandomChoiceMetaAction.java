package io.github.eggohito.neo_apoli.action.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.mixin.access.WeightedListAccessor;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.collection.WeightedList;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

public interface RandomChoiceMetaAction<A extends Action<T>, T extends ActionType<?>> extends Action<T> {

	WeightedList<A> actions();

	@Override
	default void execute(Context context) {

		actions().shuffle();
		ListIterator<A> actionIterator = actions().stream().toList().listIterator();

		if (actionIterator.hasNext())	{
			Context subContext = context.makeChild("actions[" + actionIterator.nextIndex() + "]");
			actionIterator.next().execute(subContext);
		}

	}

	@Override
	default void validate(ErrorReporter reporter) {

		List<A> actions = actions().stream().toList();

		for (int i = 0; i < actions.size(); i++) {
			actions.get(i).validate(reporter.makeChild("actions[" + i + "]"));
		}

	}

	static <A extends Action<?>, M extends RandomChoiceMetaAction<A, ?>> MapCodec<M> createCodec(Codec<A> elementCodec, Function<WeightedList<A>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			WeightedList.createCodec(elementCodec).fieldOf("actions").forGetter(RandomChoiceMetaAction::actions)
		).apply(instance, constructor));
	}

	static <B extends ByteBuf, A extends Action<?>, M extends RandomChoiceMetaAction<A, ?>> PacketCodec<B, M> createPacketCodec(PacketCodec<B, A> elementCodec, Function<WeightedList<A>, M> constructor) {
		return new PacketCodec<>() {

			@Override
			public M decode(B buf) {

				WeightedList<A> actions = new WeightedList<>();
				int size = buf.readInt();

				for (int i = 0; i < size; i++) {

					A element = elementCodec.decode(buf);
					int weight = buf.readInt();

					actions.add(element, weight);

				}

				return constructor.apply(actions);

			}

			@Override
			public void encode(B buf, M value) {

				List<WeightedList.Entry<A>> entries = ((WeightedListAccessor) value.actions()).getEntries();
				buf.writeInt(entries.size());

				for (WeightedList.Entry<A> entry : entries) {
					elementCodec.encode(buf, entry.getElement());
					buf.writeInt(entry.getWeight());
				}

			}

		};
	}

}
