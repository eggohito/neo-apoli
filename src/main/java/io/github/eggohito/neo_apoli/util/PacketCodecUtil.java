package io.github.eggohito.neo_apoli.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import io.github.eggohito.neo_apoli.mixin.access.WeightedListAccessor;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextParameter;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.collection.WeightedList;
import net.minecraft.util.function.ValueLists;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.*;

public final class PacketCodecUtil {

	public static <B extends ByteBuf, E, C extends Collection<E>> PacketCodec<B, C> nonEmptyCollection(PacketCodec<B, C> codec) {
		return validated(codec, collection -> {

			if (collection.isEmpty()) {
				throw new IllegalStateException("Collection must have contents");
			}

			else {
				return collection;
			}

		});
	}

	public static <B extends ByteBuf, E> PacketCodec<B, E> validated(PacketCodec<B, E> codec, Function<E, E> validator) {
		return codec.xmap(validator, validator);
	}

	public static <B extends PacketByteBuf, E> PacketCodec<B, E> mapped(Supplier<BiMap<String, E>> supplier) {
		return new PacketCodec<>() {

			@Override
			public E decode(B buf) {

				BiMap<String, E> mappedValues = supplier.get();
				E value = mappedValues.get(buf.readString());

				if (value != null) {
					return value;
				}

				else {
					throw new IllegalArgumentException("Expected value to be any of " + String.join(", ", mappedValues.keySet()));
				}

			}

			@Override
			public void encode(B buf, E value) {

				BiMap<String, E> mappedValues = supplier.get();
				String key = mappedValues.inverse().get(value);

				if (key != null) {
					buf.writeString(key);
				}

				else {
					throw new IllegalArgumentException("Value " + value + " is not associated with any keys!");
				}

			}

		};
	}

	public static <B extends PacketByteBuf, E> PacketCodec<B, E> mapped(BiMap<String, E> map) {
		return mapped(Suppliers.memoize(() -> map));
	}

	public static <B extends PacketByteBuf, E> PacketCodec<B, E> mapped(Consumer<ImmutableBiMap.Builder<String, E>> consumer) {

		ImmutableBiMap.Builder<String, E> builder = ImmutableBiMap.builder();
		consumer.accept(builder);

		return mapped(builder.build());

	}

	public static <B extends ByteBuf, E extends Enum<E>> PacketCodec<B, E> enumType(Class<E> enumClass) {
		return enumType(enumClass, ValueLists.OutOfBoundsHandling.CLAMP);
	}

	public static <B extends ByteBuf, E extends Enum<E>> PacketCodec<B, E> enumType(Class<E> enumClass, ValueLists.OutOfBoundsHandling oobHandler) {

		ToIntFunction<E> toOrdinal = Enum::ordinal;
		IntFunction<E> fromOrdinal = ValueLists.createIndexToValueFunction(toOrdinal, enumClass.getEnumConstants(), oobHandler);

		return PacketCodecs.indexed(fromOrdinal, toOrdinal).cast();

	}

	public static <B extends ByteBuf, A> PacketCodec<B, A> lazy(Supplier<PacketCodec<B, A>> delegate) {
		return lazy(delegate.toString(), delegate);
	}

	public static <B extends ByteBuf, A> PacketCodec<B, A> lazy(String name, Supplier<PacketCodec<B, A>> delegate) {
		return new PacketCodec<>() {

			@Override
			public A decode(B buf) {
				return delegate.get().decode(buf);
			}

			@Override
			public void encode(B buf, A value) {
				delegate.get().encode(buf, value);
			}

			@Override
			public String toString() {
				return "LazyPacketCodec[" + name + "]";
			}

		};
	}

	public static <B extends ByteBuf, T> PacketCodec<B, T> unit(Supplier<T> supplier) {
		return PacketCodec.ofStatic((buf, value) -> supplier.get(), buf -> supplier.get());
	}

	public static <B extends ByteBuf, T> PacketCodec<B, WeightedList<T>> weightedList(PacketCodec<B, T> entryCodec) {
		return new PacketCodec<>() {

			@Override
			public WeightedList<T> decode(B buf) {

				WeightedList<T> entries = new WeightedList<>();
				int size = buf.readInt();

				for (int i = 0; i < size; i++) {

					T entry = entryCodec.decode(buf);
					int weight = buf.readInt();

					entries.add(entry, weight);

				}

				return entries;

			}

			@Override
			public void encode(B buf, WeightedList<T> value) {

				List<WeightedList.Entry<T>> entries = ((WeightedListAccessor) value).getEntries();
				buf.writeInt(entries.size());

				for (var entry : entries) {
					entryCodec.encode(buf, entry.getElement());
					buf.writeInt(entry.getWeight());
				}

			}

		};
	}

	public static <T> PacketCodec<RegistryByteBuf, TypedContextParameter<T>> createParameterCodec(String name, Class<T> typeClass) {
		return NeoApoliContextParameters.PACKET_CODEC.xmap(
			parameter -> {

				if (typeClass.isAssignableFrom(parameter.getTypeClass())) {
					//noinspection unchecked
					return (TypedContextParameter<T>) parameter;
				}

				else {
					throw new IllegalArgumentException("Unknown " + name.toLowerCase(Locale.ROOT) + " parameter with ID: \"" + parameter.getId() + "\"");
				}

			},
			Function.identity()
		);
	}

}
