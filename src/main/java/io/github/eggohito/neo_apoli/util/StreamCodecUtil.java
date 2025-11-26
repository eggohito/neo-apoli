package io.github.eggohito.neo_apoli.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import io.github.eggohito.neo_apoli.mixin.access.ShufflingListAccessor;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.entity.ai.behavior.ShufflingList;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.*;

public final class StreamCodecUtil {

	public static <B extends ByteBuf, E, C extends Collection<E>> StreamCodec<B, C> nonEmptyCollection(StreamCodec<B, C> codec) {
		return validated(codec, collection -> {

			if (collection.isEmpty()) {
				throw new IllegalStateException("Collection must have contents");
			}

			else {
				return collection;
			}

		});
	}

	public static <B extends ByteBuf, E> StreamCodec<B, E> validated(StreamCodec<B, E> codec, Function<E, E> validator) {
		return codec.map(validator, validator);
	}

	public static <B extends FriendlyByteBuf, E> StreamCodec<B, E> mapped(Supplier<BiMap<String, E>> supplier) {
		return new StreamCodec<>() {

			@Override
			public E decode(B buf) {

				BiMap<String, E> mappedValues = supplier.get();
				E value = mappedValues.get(buf.readUtf());

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
					buf.writeUtf(key);
				}

				else {
					throw new IllegalArgumentException("Value " + value + " is not associated with any keys!");
				}

			}

		};
	}

	public static <B extends FriendlyByteBuf, E> StreamCodec<B, E> mapped(BiMap<String, E> map) {
		return mapped(Suppliers.memoize(() -> map));
	}

	public static <B extends FriendlyByteBuf, E> StreamCodec<B, E> mapped(Consumer<ImmutableBiMap.Builder<String, E>> consumer) {

		ImmutableBiMap.Builder<String, E> builder = ImmutableBiMap.builder();
		consumer.accept(builder);

		return mapped(builder.build());

	}

	public static <B extends ByteBuf, E extends Enum<E>> StreamCodec<B, E> enumType(Class<E> enumClass) {
		return enumType(enumClass, ByIdMap.OutOfBoundsStrategy.CLAMP);
	}

	public static <B extends ByteBuf, E extends Enum<E>> StreamCodec<B, E> enumType(Class<E> enumClass, ByIdMap.OutOfBoundsStrategy oobHandler) {

		ToIntFunction<E> toOrdinal = Enum::ordinal;
		IntFunction<E> fromOrdinal = ByIdMap.continuous(toOrdinal, enumClass.getEnumConstants(), oobHandler);

		return ByteBufCodecs.idMapper(fromOrdinal, toOrdinal).cast();

	}

	public static <B extends ByteBuf, A> StreamCodec<B, A> lazy(Supplier<StreamCodec<B, A>> delegate) {
		return lazy(delegate.toString(), delegate);
	}

	public static <B extends ByteBuf, A> StreamCodec<B, A> lazy(String name, Supplier<StreamCodec<B, A>> delegate) {
		return new StreamCodec<>() {

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

	public static <B extends ByteBuf, T> StreamCodec<B, T> unit(Supplier<T> supplier) {
		return StreamCodec.of((buf, value) -> supplier.get(), buf -> supplier.get());
	}

	public static <B extends ByteBuf, T> StreamCodec<B, ShufflingList<T>> weightedList(StreamCodec<B, T> entryCodec) {
		return new StreamCodec<>() {

			@Override
			public ShufflingList<T> decode(B buf) {

				ShufflingList<T> entries = new ShufflingList<>();
				int size = buf.readInt();

				for (int i = 0; i < size; i++) {

					T entry = entryCodec.decode(buf);
					int weight = buf.readInt();

					entries.add(entry, weight);

				}

				return entries;

			}

			@Override
			public void encode(B buf, ShufflingList<T> value) {

				List<ShufflingList.WeightedEntry<T>> entries = ((ShufflingListAccessor) value).getEntries();
				buf.writeInt(entries.size());

				for (var entry : entries) {
					entryCodec.encode(buf, entry.getData());
					buf.writeInt(entry.getWeight());
				}

			}

		};
	}

	public static <T> StreamCodec<RegistryFriendlyByteBuf, TypedContextKey<T>> createParameterCodec(String name, Class<T> typeClass) {
		return NeoApoliContextKeys.STREAM_CODEC.map(
			parameter -> {

				if (typeClass.isAssignableFrom(parameter.getTypeClass())) {
					//noinspection unchecked
					return (TypedContextKey<T>) parameter;
				}

				else {
					throw new IllegalArgumentException("Unknown " + name.toLowerCase(Locale.ROOT) + " parameter with ID: \"" + parameter.name() + "\"");
				}

			},
			Function.identity()
		);
	}

}
