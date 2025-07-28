package io.github.eggohito.neo_apoli.provider.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.ListIterator;
import java.util.function.*;

public interface IfElseListMetaValueProvider<P extends ValueProvider<V>, V> {

	List<Entry<P>> entries();

	@ApiStatus.Internal
	default V internalImpl(Context context, Supplier<V> defaultValue) {
		return this.internalImpl(context, ValueProvider::next, defaultValue);
	}

	@ApiStatus.Internal
	default <NV> NV internalImpl(Context context, BiFunction<P, Context, NV> getter, Supplier<NV> defaultValue) {

		MutableBoolean continueCondition = new MutableBoolean(false);
		MutableObject<NV> result = new MutableObject<>(defaultValue.get());

		BiConsumer<Integer, Entry<P>> processor = (index, entry) -> {

			Context subContext = context.makeChild(".values[" + index + "]");
			boolean shouldProvide = entry.condition().test(subContext.makeChild(".condition"));

			if (shouldProvide) {
				result.setValue(getter.apply(entry.value(), subContext.makeChild(".value")));
			}

			continueCondition.setValue(shouldProvide);

		};

		this.iterate(processor, continueCondition::getValue);
		return result.getValue();

	}

	default void validate(ContextAware.ErrorReporter reporter) {
		this.iterate((index, entry) -> entry.validate(reporter.makeChild(".values[" + index + "]")));
	}

	default void iterate(BiConsumer<Integer, Entry<P>> processor, BooleanSupplier continueCondition) {

		ListIterator<Entry<P>> entryIterator = entries().listIterator();
		boolean init = false;

		while (entryIterator.hasNext()) {

			if (init && !continueCondition.getAsBoolean()) {
				break;
			}

			processor.accept(entryIterator.nextIndex(), entryIterator.next());
			init = true;

		}

	}

	default void iterate(BiConsumer<Integer, Entry<P>> processor) {
		this.iterate(processor, () -> true);
	}

	static <P extends ValueProvider<V>, V, M extends IfElseListMetaValueProvider<P, V>> MapCodec<M> codec(Codec<P> providerCodec, Function<List<Entry<P>>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codecs.nonEmptyList(Entry.codec(providerCodec).listOf()).fieldOf("entries").forGetter(IfElseListMetaValueProvider::entries)
		).apply(instance, constructor));
	}

	static <P extends ValueProvider<V>, V, M extends IfElseListMetaValueProvider<P, V>> PacketCodec<RegistryByteBuf, M> packetCodec(PacketCodec<RegistryByteBuf, P> providerCodec, Function<List<Entry<P>>, M> constructor) {
		PacketCodec<RegistryByteBuf, List<Entry<P>>> entriesCodec = PacketCodecs.collection(ObjectArrayList::new, Entry.packetCodec(providerCodec));
		return entriesCodec.xmap(constructor, IfElseListMetaValueProvider::entries);
	}

	record Entry<P extends ValueProvider<?>>(Condition condition, P value) {

		public void validate(ContextAware.ErrorReporter reporter) {
			condition().validate(reporter.makeChild(".condition"));
			value().validate(reporter.makeChild(".value"));
		}

		public static <P extends ValueProvider<?>> Codec<Entry<P>> codec(Codec<P> codec) {
			return RecordCodecBuilder.create(instance -> instance.group(
				Condition.CODEC.fieldOf("condition").forGetter(Entry::condition),
				codec.fieldOf("value").forGetter(Entry::value)
			).apply(instance, Entry::new));
		}

		public static <P extends ValueProvider<?>> PacketCodec<RegistryByteBuf, Entry<P>> packetCodec(PacketCodec<RegistryByteBuf, P> codec) {
			return PacketCodec.tuple(
				Condition.PACKET_CODEC, Entry::condition,
				codec, Entry::value,
				Entry::new
			);
		}

	}

}
