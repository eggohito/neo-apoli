package io.github.eggohito.neo_apoli.provider.misc;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface MultiNumberProvider extends NumberProvider {

	List<NumberProvider> numbers();

	default void iterate(BiConsumer<Integer, NumberProvider> processor) {

		ListIterator<NumberProvider> numberIterator = numbers().listIterator();

		while (numberIterator.hasNext()) {
			processor.accept(numberIterator.nextIndex(), numberIterator.next());
		}

	}

	default <N extends Number> N iterateAndProcess(Context context, BiFunction<NumberProvider, Context, N> valueGetter, BiFunction<N, N, N> processor, N initialValue) {

		AtomicReference<N> result = new AtomicReference<>(initialValue);
		MutableBoolean init = new MutableBoolean(false);

		this.iterate((index, number) -> {

			N previousValue = result.get();
			N nextValue = valueGetter.apply(number, context.makeChild("numbers[" + index + "]"));

			if (init.isTrue()) {
				result.set(processor.apply(previousValue, nextValue));
			}

			else {
				result.set(nextValue);
				init.setTrue();
			}

		});

		return result.get();

	}

	@Override
	default void validate(ErrorReporter reporter) {
		this.iterate((index, number) -> number.validate(reporter.makeChild("numbers[" + index + "]")));
	}

	static <M extends MultiNumberProvider> MapCodec<M> simpleCodec(Function<List<NumberProvider>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> addNumbersField(instance).apply(instance, constructor));
	}

	static <M extends MultiNumberProvider> PacketCodec<RegistryByteBuf, M> simplePacketCodec(Function<List<NumberProvider>, M> constructor) {
		return createPacketCodec((buf, m) -> {}, (buf, numberProviders) -> constructor.apply(numberProviders));
	}

	static <M extends MultiNumberProvider> Products.P1<RecordCodecBuilder.Mu<M>, List<NumberProvider>> addNumbersField(RecordCodecBuilder.Instance<M> instance) {
		return instance.group(
			Codec.lazyInitialized(() -> NumberProvider.CODEC).listOf().fieldOf("numbers").forGetter(MultiNumberProvider::numbers)
		);
	}

	static <M extends MultiNumberProvider> PacketCodec<RegistryByteBuf, M> createPacketCodec(BiConsumer<RegistryByteBuf, M> encoder, BiFunction<RegistryByteBuf, List<NumberProvider>, M> decoder) {
		Supplier<PacketCodec<RegistryByteBuf, List<NumberProvider>>> packetCodecSupplier = () -> PacketCodecs.collection(ObjectArrayList::new, NumberProvider.PACKET_CODEC);
		return PacketCodec.ofStatic(
			(buf, value) -> {
				packetCodecSupplier.get().encode(buf, value.numbers());
				encoder.accept(buf, value);
			},
			buf -> {
				List<NumberProvider> numbers = packetCodecSupplier.get().decode(buf);
				return decoder.apply(buf, numbers);
			}
		);
	}

}
