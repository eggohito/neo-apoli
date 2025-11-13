package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

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

			Context conditionContext = context.makeChild(".conditions[" + index + "]");
			boolean result = condition.test(conditionContext);

			if (!conditionContext.hasErrors() && result) {
				return true;
			}

		}

		return false;

	}

	@Override
	default void validate(ErrorReporter reporter) {

		ListIterator<C> listIterator = conditions().listIterator();

		while (listIterator.hasNext()) {

			int index = listIterator.nextIndex();
			C condition = listIterator.next();

			condition.validate(reporter.makeChild(".conditions[" + index + "]"));

		}

	}

	static <C extends Condition, M extends AnyOfMetaCondition<C>> MapCodec<M> codec(Codec<C> conditionCodec, java.util.function.Function<List<C>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			conditionCodec.listOf().fieldOf("conditions").forGetter(AnyOfMetaCondition::conditions)
		).apply(instance, constructor));
	}

	static <C extends Condition, M extends AnyOfMetaCondition<C>> PacketCodec<RegistryByteBuf, M> packetCodec(PacketCodec<RegistryByteBuf, C> conditionCodec, Function<List<C>, M> constructor) {
		return PacketCodec.tuple(
			PacketCodecs.collection(ObjectArrayList::new, conditionCodec), AnyOfMetaCondition::conditions,
			constructor
		);
	}

}
