package io.github.eggohito.neo_apoli.condition.meta;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.booleans.BooleanPredicate;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.List;
import java.util.ListIterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;

public interface MultiMetaCondition<C extends Condition> {

	List<C> conditions();

	default void validate(ContextAware.ErrorReporter reporter) {
		this.iterate((index, condition) -> condition.validate(reporter.makeChild(".conditions[" + index + "]")), () -> true);
	}

	default void iterate(BiConsumer<Integer, C> processor, BooleanSupplier continueCondition) {

		ListIterator<C> conditionIterator = conditions().listIterator();

		while (conditionIterator.hasNext()) {

			processor.accept(conditionIterator.nextIndex(), conditionIterator.next());

			if (!continueCondition.getAsBoolean()) {
				break;
			}

		}

	}

	default boolean iterateAndProcess(Context context, BiPredicate<Boolean, Boolean> tester, BooleanPredicate continueCondition, boolean initialValue) {

		MutableBoolean result = new MutableBoolean(initialValue);
		MutableBoolean init = new MutableBoolean(false);

		this.iterate((index, condition) -> {

			boolean previousResult = result.booleanValue();
			boolean nextResult = condition.test(context.makeChild(".conditions[" + index + "]"));

			if (init.isTrue()) {
				result.setValue(tester.test(previousResult, nextResult));
			}

			else {
				result.setValue(nextResult);
				init.setTrue();
			}

		}, () -> continueCondition.test(result.booleanValue()));

		return result.booleanValue();

	}

	static <C extends Condition, M extends MultiMetaCondition<C>> Products.P1<RecordCodecBuilder.Mu<M>, List<C>> addConditionsField(Codec<C> conditionCodec, RecordCodecBuilder.Instance<M> instance) {
		return instance.group(
			conditionCodec.listOf().fieldOf("conditions").forGetter(MultiMetaCondition::conditions)
		);
	}

	static <B extends ByteBuf, C extends Condition, M extends MultiMetaCondition<C>> PacketCodec<B, M> createConditionsPacketCodec(PacketCodec<B, C> conditionCodec, BiConsumer<B, M> encoder, BiFunction<B, List<C>, M> decoder) {
		PacketCodec<B, List<C>> packetCodec = PacketCodecs.collection(ObjectArrayList::new, conditionCodec);
		return PacketCodec.ofStatic(
			(buf, value) -> {
				packetCodec.encode(buf, value.conditions());
				encoder.accept(buf, value);
			},
			buf -> {
				List<C> conditions = packetCodec.decode(buf);
				return decoder.apply(buf, conditions);
			}
		);
	}

}
