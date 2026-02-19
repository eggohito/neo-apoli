package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

public interface AllOfMetaCondition<C extends Condition> extends Condition {

	List<C> conditions();

	@Override
	default boolean test(Context context) {

		ListIterator<C> listIterator = conditions().listIterator();

		while (listIterator.hasNext()) {

			Context conditionContext = context.forChild(".conditions[" + listIterator.nextIndex() + "]");
			C condition = listIterator.next();

			if (!condition.test(conditionContext) && !conditionContext.hasErrors()) {
				return false;
			}

		}

		return true;

	}

	@Override
	default void validate(Context.Validator validator) {
		Condition.super.validate(validator);
		ContextHelper.validateAll(conditions(), validator, index -> ".conditions[" + index + "]");
	}

	static <C extends Condition, M extends AllOfMetaCondition<C>> MapCodec<M> mapCodec(Codec<C> conditionCodec, Function<List<C>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			conditionCodec.listOf().fieldOf("conditions").forGetter(AllOfMetaCondition::conditions)
		).apply(instance, constructor));
	}

	static <C extends Condition, M extends AllOfMetaCondition<C>> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(StreamCodec<RegistryFriendlyByteBuf, C> conditionCodec, Function<List<C>, M> constructor) {
		return StreamCodec.composite(
			ByteBufCodecs.collection(ObjectArrayList::new, conditionCodec), AllOfMetaCondition::conditions,
			constructor
		);
	}

}
