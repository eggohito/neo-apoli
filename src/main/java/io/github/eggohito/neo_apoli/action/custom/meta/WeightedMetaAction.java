package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.util.BiIntegerConsumer;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;

import java.util.function.Consumer;
import java.util.function.Function;

public interface WeightedMetaAction<A extends Action> extends Action {

	WeightedList<A> entries();

	@Override
	default void execute(Context context) {
		entries().neo_apoli$getRandomAndIndex(context.level().getRandom()).ifPresent(acceptSingle(context));
	}

	@Override
	default void validate(Context.Validator validator) {
		Action.super.validate(validator);
		MiscUtil.iterateList(entries().unwrap(), validateSingle(validator));
	}

	private Consumer<ObjectIntPair<A>> acceptSingle(Context context) {
		return indexAndAction -> indexAndAction.first().execute(context.forChild(".entries[" + indexAndAction.secondInt() + "]"));
	}

	private BiIntegerConsumer<Weighted<A>> validateSingle(Context.Validator validator) {
		return (index, weighted) -> weighted.value().validate(validator.forChild(".entries[" + index + "]"));
	}

	static <A extends Action, M extends WeightedMetaAction<A>> MapCodec<M> mapCodec(Codec<A> entryCodec, Function<WeightedList<A>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			WeightedList.codec(entryCodec).fieldOf("entries").forGetter(WeightedMetaAction::entries)
		).apply(instance, constructor));
	}

	static <A extends Action, M extends WeightedMetaAction<A>> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(StreamCodec<RegistryFriendlyByteBuf, A> entryCodec, Function<WeightedList<A>, M> constructor) {
		return StreamCodec.composite(
			StreamCodecUtil.weightedList(entryCodec), WeightedMetaAction::entries,
			constructor
		);
	}

}
