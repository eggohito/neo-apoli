package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.context.Context;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

public interface ISequenceMetaAction<A extends Action> extends MetaAction {

	List<A> actions();

	@Override
	default void execute(Context context) {

		ListIterator<A> listIterator = actions().listIterator();

		while (listIterator.hasNext()) {

			Context actionContext = context.forChild(".actions[" + listIterator.nextIndex() + "]");

			listIterator.next().execute(actionContext);

		}

	}

	@Override
	default void validate(Context.Validator validator) {

		ListIterator<A> listIterator = actions().listIterator();

		while (listIterator.hasNext()) {

			Context.Validator actionValidator = validator.forChild(".actions[" + listIterator.nextIndex() + "]");

			listIterator.next().validate(actionValidator);

		}

	}

	static <A extends Action, M extends ISequenceMetaAction<A>> MapCodec<M> mapCodec(Codec<A> actionCodec, Function<List<A>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			actionCodec.listOf().fieldOf("actions").forGetter(ISequenceMetaAction::actions)
		).apply(instance, constructor));
	}

	static <A extends Action, M extends ISequenceMetaAction<A>> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(StreamCodec<RegistryFriendlyByteBuf, A> actionCodec, Function<List<A>, M> constructor) {
		return StreamCodec.composite(
			ByteBufCodecs.collection(ObjectArrayList::new, actionCodec), ISequenceMetaAction::actions,
			constructor
		);
	}

}
