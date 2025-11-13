package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.util.context.Context;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

public interface SequenceMetaAction<A extends Action> extends MetaAction {

	List<A> actions();

	@Override
	default void execute(Context context) {

		ListIterator<A> listIterator = actions().listIterator();

		while (listIterator.hasNext()) {

			int index = listIterator.nextIndex();
			A action = listIterator.next();

			action.execute(context.makeChild(".actions[" + index + "]"));

		}


	}

	@Override
	default void validate(ErrorReporter reporter) {

		ListIterator<A> listIterator = actions().listIterator();

		while (listIterator.hasNext()) {

			int index = listIterator.nextIndex();
			A action = listIterator.next();

			action.validate(reporter.makeChild(".actions[" + index + "]"));

		}

	}

	static <A extends Action, M extends SequenceMetaAction<A>> MapCodec<M> codec(Codec<A> actionCodec, Function<List<A>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			actionCodec.listOf().fieldOf("actions").forGetter(SequenceMetaAction::actions)
		).apply(instance, constructor));
	}

	static <A extends Action, M extends SequenceMetaAction<A>> PacketCodec<RegistryByteBuf, M> packetCodec(PacketCodec<RegistryByteBuf, A> actionCodec, Function<List<A>, M> constructor) {
		return PacketCodec.tuple(
			PacketCodecs.collection(ObjectArrayList::new, actionCodec), SequenceMetaAction::actions,
			constructor
		);
	}

}
