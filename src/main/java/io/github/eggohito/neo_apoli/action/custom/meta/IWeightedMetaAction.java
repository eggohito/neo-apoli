package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.behavior.ShufflingList;

import java.util.ListIterator;
import java.util.function.Function;

public interface IWeightedMetaAction<A extends Action> extends MetaAction {

	ShufflingList<A> entries();

	@Override
	default void execute(Context context) {

		entries().shuffle();
		ListIterator<A> listIterator = entries().stream().toList().listIterator();

		if (listIterator.hasNext()) {

			Context entryContext = context.forChild(".entries[" + listIterator.nextIndex() + "]");

			listIterator.next().execute(entryContext);

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

	static <A extends Action, M extends IWeightedMetaAction<A>> MapCodec<M> mapCodec(Codec<A> entryCodec, Function<ShufflingList<A>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			ShufflingList.codec(entryCodec).fieldOf("entries").forGetter(IWeightedMetaAction::entries)
		).apply(instance, constructor));
	}

	static <A extends Action, M extends IWeightedMetaAction<A>> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(StreamCodec<RegistryFriendlyByteBuf, A> entryCodec, Function<ShufflingList<A>, M> constructor) {
		return StreamCodec.composite(
			StreamCodecUtil.weightedList(entryCodec), IWeightedMetaAction::entries,
			constructor
		);
	}

}
