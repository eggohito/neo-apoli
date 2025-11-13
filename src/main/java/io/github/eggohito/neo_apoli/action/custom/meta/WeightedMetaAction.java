package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.collection.WeightedList;

import java.util.ListIterator;
import java.util.function.Function;

public interface WeightedMetaAction<A extends Action> extends MetaAction {

	WeightedList<A> entries();

	@Override
	default void execute(Context context) {

		entries().shuffle();
		ListIterator<A> listIterator = entries().stream().toList().listIterator();

		if (listIterator.hasNext()) {

			int index = listIterator.nextIndex();
			A entry = listIterator.next();

			entry.execute(context.makeChild(".entries[" + index + "]"));

		}

	}

	@Override
	default void validate(ErrorReporter reporter) {

		ListIterator<A> listIterator = this.entries().stream().toList().listIterator();

		while (listIterator.hasNext()) {

			int index = listIterator.nextIndex();
			A entry = listIterator.next();

			entry.validate(reporter.makeChild(".entries[" + index + "]"));

		}

	}

	static <A extends Action, M extends WeightedMetaAction<A>> MapCodec<M> codec(Codec<A> entryCodec, Function<WeightedList<A>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			WeightedList.createCodec(entryCodec).fieldOf("entries").forGetter(WeightedMetaAction::entries)
		).apply(instance, constructor));
	}

	static <A extends Action, M extends WeightedMetaAction<A>> PacketCodec<RegistryByteBuf, M> packetCodec(PacketCodec<RegistryByteBuf, A> entryCodec, Function<WeightedList<A>, M> constructor) {
		return PacketCodec.tuple(
			PacketCodecUtil.weightedList(entryCodec), WeightedMetaAction::entries,
			constructor
		);
	}

}
