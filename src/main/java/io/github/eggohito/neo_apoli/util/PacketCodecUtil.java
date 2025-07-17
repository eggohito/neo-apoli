package io.github.eggohito.neo_apoli.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.function.ValueLists;

import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public final class PacketCodecUtil {

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

		ToIntFunction<E> toOrdinal = Enum::ordinal;
		IntFunction<E> fromOrdinal = ValueLists.createIndexToValueFunction(toOrdinal, enumClass.getEnumConstants(), ValueLists.OutOfBoundsHandling.CLAMP);

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
				return "RecursivePacketCodec[" + name + "]";
			}

		};
	}

	public static <B extends ByteBuf, T> PacketCodec<B, T> unit(Supplier<T> supplier) {
		return PacketCodec.ofStatic((buf, value) -> supplier.get(), buf -> supplier.get());
	}

}
