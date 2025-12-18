package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.behavior.ShufflingList;

import java.util.ListIterator;
import java.util.function.Function;

public interface WeightedMetaAction<A extends Action> extends MetaAction {

	ShufflingList<A> entries();

	@Override
	default void execute(Context context) {

		entries().shuffle();
		ListIterator<A> listIterator = entries().stream().toList().listIterator();

		if (listIterator.hasNext()) {

			int index = listIterator.nextIndex();
			A entry = listIterator.next();

			entry.execute(context.forChild(".entries[" + index + "]"));

		}

	}

	@Override
	default void validate(Context.Validator validator) {

		ListIterator<A> listIterator = this.entries().stream().toList().listIterator();

		while (listIterator.hasNext()) {

			int index = listIterator.nextIndex();
			A entry = listIterator.next();

			entry.validate(validator.forChild(".entries[" + index + "]"));

		}

	}

	static <A extends Action, M extends WeightedMetaAction<A>> MapCodec<M> createCodec(Codec<A> entryCodec, Function<ShufflingList<A>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			ShufflingList.codec(entryCodec).fieldOf("entries").forGetter(WeightedMetaAction::entries)
		).apply(instance, constructor));
	}

	static <A extends Action, M extends WeightedMetaAction<A>> StreamCodec<RegistryFriendlyByteBuf, M> createStreamCodec(StreamCodec<RegistryFriendlyByteBuf, A> entryCodec, Function<ShufflingList<A>, M> constructor) {
		return StreamCodec.composite(
			StreamCodecUtil.weightedList(entryCodec), WeightedMetaAction::entries,
			constructor
		);
	}

}
