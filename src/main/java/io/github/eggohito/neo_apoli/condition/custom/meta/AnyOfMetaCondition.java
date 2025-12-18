package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.util.context.Context;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

public interface AnyOfMetaCondition<C extends Condition> extends MetaCondition {

	List<C> conditions();

	@Override
	default boolean test(Context context) {

		ListIterator<C> listIterator = conditions().listIterator();

		while (listIterator.hasNext()) {

			int index = listIterator.nextIndex();
			C condition = listIterator.next();

			Context conditionContext = context.forChild(".conditions[" + index + "]");
			boolean result = condition.test(conditionContext);

			if (!conditionContext.hasErrors() && result) {
				return true;
			}

		}

		return false;

	}

	@Override
	default void validate(Context.Validator validator) {

		ListIterator<C> listIterator = conditions().listIterator();

		while (listIterator.hasNext()) {

			int index = listIterator.nextIndex();
			C condition = listIterator.next();

			condition.validate(validator.forChild(".conditions[" + index + "]"));

		}

	}

	static <C extends Condition, M extends AnyOfMetaCondition<C>> MapCodec<M> createCodec(Codec<C> conditionCodec, java.util.function.Function<List<C>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			conditionCodec.listOf().fieldOf("conditions").forGetter(AnyOfMetaCondition::conditions)
		).apply(instance, constructor));
	}

	static <C extends Condition, M extends AnyOfMetaCondition<C>> StreamCodec<RegistryFriendlyByteBuf, M> createStreamCodec(StreamCodec<RegistryFriendlyByteBuf, C> conditionCodec, Function<List<C>, M> constructor) {
		return StreamCodec.composite(
			ByteBufCodecs.collection(ObjectArrayList::new, conditionCodec), AnyOfMetaCondition::conditions,
			constructor
		);
	}

}
