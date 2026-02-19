package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextHelper;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.function.Function;

public interface SequenceMetaAction<A extends Action> extends Action {

	List<A> actions();

	@Override
	default void execute(Context context) {
		MiscUtil.iterateList(actions(), (index, action) -> action.execute(context.forChild(".actions[" + index + "]")));
	}

	@Override
	default void validate(Context.Validator validator) {
		Action.super.validate(validator);
		ContextHelper.validateAll(actions(), validator, index -> ".actions[" + index + "]");
	}

	static <A extends Action, M extends SequenceMetaAction<A>> MapCodec<M> mapCodec(Codec<A> actionCodec, Function<List<A>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			actionCodec.listOf().fieldOf("actions").forGetter(SequenceMetaAction::actions)
		).apply(instance, constructor));
	}

	static <A extends Action, M extends SequenceMetaAction<A>> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(StreamCodec<RegistryFriendlyByteBuf, A> actionCodec, Function<List<A>, M> constructor) {
		return StreamCodec.composite(
			actionCodec.apply(ByteBufCodecs.list()), SequenceMetaAction::actions,
			constructor
		);
	}

}
