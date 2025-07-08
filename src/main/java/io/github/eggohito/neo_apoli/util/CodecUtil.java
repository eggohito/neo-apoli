package io.github.eggohito.neo_apoli.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.codec.FilteredUnboundedMapCodec;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.function.ValueLists;

import java.util.function.*;

public class CodecUtil {

	public static <K, V> FilteredUnboundedMapCodec<K, V> filteredUnboundedMap(final Codec<K> keyCodec, final Codec<V> elementCodec, Predicate<K> keyFilter) {
		return new FilteredUnboundedMapCodec<>(keyCodec, elementCodec, keyFilter);
	}

	public static <E> Codec<E> mapped(Supplier<BiMap<String, E>> supplier) {
		return Codec.STRING.flatXmap(
			str -> {

				BiMap<String, E> mappedValues = supplier.get();
				E value = mappedValues.get(str);

				if (value != null) {
					return DataResult.success(value);
				}

				else {
					return DataResult.error(() -> "Expected value to be any of " + String.join(", ", mappedValues.keySet()));
				}

			},
			e -> {

				BiMap<String, E> mappedValues = supplier.get();
				String key = mappedValues.inverse().get(e);

				if (key != null) {
					return DataResult.success(key);
				}

				else {
					return DataResult.error(() -> "Value " + e + " is not associated to any keys!");
				}

			}
		);
	}

	public static <E> Codec<E> mapped(BiMap<String, E> map) {
		return mapped(Suppliers.memoize(() -> map));
	}

	public static <E> Codec<E> mapped(Consumer<ImmutableBiMap.Builder<String, E>> consumer) {

		ImmutableBiMap.Builder<String, E> builder = ImmutableBiMap.builder();
		consumer.accept(builder);

		return mapped(builder.build());

	}

	public static <E extends Enum<E>> Codec<E> enumType(Class<E> enumClass) {

		E[] enumConstants = enumClass.getEnumConstants();

		ToIntFunction<E> toOrdinal = Enum::ordinal;
		IntFunction<E> fromOrdinal = ValueLists.createIndexToValueFunction(toOrdinal, enumConstants, ValueLists.OutOfBoundsHandling.CLAMP);

		Function<E, String> toString = enumConstant -> enumConstant instanceof StringIdentifiable stringIdentifiable
			? stringIdentifiable.asString()
			: enumConstant.name();
		Function<String, E> fromString = name -> {

			for (E enumConstant : enumConstants) {

				boolean matches = enumConstant.name().equalsIgnoreCase(name);

				if (!matches && enumConstant instanceof StringIdentifiable stringIdentifiable) {
					matches = stringIdentifiable.asString().equalsIgnoreCase(name);
				}

				if (matches) {
					return enumConstant;
				}

			}

			return null;

		};

		return Codecs.orCompressed(
			Codec.stringResolver(toString, fromString),
			Codecs.rawIdChecked(toOrdinal, fromOrdinal, -1)
		);

	}

}
