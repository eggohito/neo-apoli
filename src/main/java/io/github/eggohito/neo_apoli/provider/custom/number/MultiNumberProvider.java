package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.List;
import java.util.ListIterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface MultiNumberProvider extends NumberProvider {

	List<NumberProvider> numbers();

	@Override
	default void validate(ProblemReporter reporter) {
		NumberProvider.super.validate(reporter);
		this.iterate((index, number) -> number.validate(reporter.forChild(".numbers[" + index + "]")));
	}

	default <N extends Number> N iterateAndProcess(Context context, BiFunction<NumberProvider, Context, N> getter, BiFunction<N, N, N> processor, N initialValue) {

		MutableObject<N> result = new MutableObject<>(initialValue);
		MutableBoolean init = new MutableBoolean(false);

		this.iterate((index, number) -> {

			Context numberContext = context.forChild(".numbers[" + index + "]");
			N value = getter.apply(number, numberContext);

			if (!numberContext.hasErrors()) {

				if (init.isTrue()) {
					result.setValue(processor.apply(result.getValue(), value));
				}

				else {
					result.setValue(value);
					init.setTrue();
				}

			}

		});

		return result.getValue();

	}

	default void iterate(BiConsumer<Integer, NumberProvider> processor) {

		ListIterator<NumberProvider> listIterator = numbers().listIterator();

		while (listIterator.hasNext()) {
			processor.accept(listIterator.nextIndex(), listIterator.next());
		}

	}

	static <M extends MultiNumberProvider> MapCodec<M> codec(Function<List<NumberProvider>, M> constructor) {
		return MapCodecUtil.lazy(() -> RecordCodecBuilder.mapCodec(instance -> instance.group(
			NumberProvider.CODEC.listOf().fieldOf("numbers").forGetter(MultiNumberProvider::numbers)
		).apply(instance, constructor)));
	}

	static <M extends MultiNumberProvider> StreamCodec<RegistryFriendlyByteBuf, M> packetCodec(Function<List<NumberProvider>, M> constructor) {
		return StreamCodecUtil.lazy(() -> StreamCodec.composite(
			ByteBufCodecs.collection(ObjectArrayList::new, NumberProvider.STREAM_CODEC), MultiNumberProvider::numbers,
			constructor
		));
	}

}
