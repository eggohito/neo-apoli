package io.github.eggohito.neo_apoli.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.codec.FilteredUnboundedMapCodec;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
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
		return enumType(enumClass, ByIdMap.OutOfBoundsStrategy.CLAMP);
	}

	public static <E extends Enum<E>> Codec<E> enumType(Class<E> enumClass, ByIdMap.OutOfBoundsStrategy oobHandler) {

		E[] enumConstants = enumClass.getEnumConstants();

		ToIntFunction<E> toOrdinal = Enum::ordinal;
		IntFunction<E> fromOrdinal = ByIdMap.continuous(toOrdinal, enumConstants, oobHandler);

		Function<E, String> toString = enumConstant -> (enumConstant instanceof StringRepresentable stringIdentifiable
			? stringIdentifiable.getSerializedName()
			: enumConstant.name()).toLowerCase(Locale.ROOT);
		Function<String, E> fromString = name -> {

			for (E enumConstant : enumConstants) {

				boolean matches = enumConstant.name().equalsIgnoreCase(name);

				if (!matches && enumConstant instanceof StringRepresentable stringIdentifiable) {
					matches = stringIdentifiable.getSerializedName().equalsIgnoreCase(name);
				}

				if (matches) {
					return enumConstant;
				}

			}

			return null;

		};

		return ExtraCodecs.orCompressed(
			Codec.stringResolver(toString, fromString),
			ExtraCodecs.idResolverCodec(toOrdinal, fromOrdinal, -1)
		);

	}

	public static <T> Codec<TypedContextKey<T>> createContextKeyCodec(String name, Class<T> typeClass) {
		return NeoApoliContextKeys.CODEC.comapFlatMap(
			parameter -> {

				if (typeClass.isAssignableFrom(parameter.getTypeClass())) {
					//noinspection unchecked
					return DataResult.success((TypedContextKey<T>) parameter);
				}

				else {
					return DataResult.error(() -> "Unknown " + name.toLowerCase(Locale.ROOT) + " parameter with ID: \"" + parameter.name() + "\"");
				}

			},
			Function.identity()
		);
	}

	public static <T> Codec<TagKey<T>> hashedTag(ResourceKey<? extends Registry<T>> registryRef, String defaultNamespace) {
		return Codec.STRING.comapFlatMap(
			string -> string.startsWith("#")
				? DynamicResourceLocation.parse(string.substring(1), defaultNamespace).map(location -> TagKey.create(registryRef, location))
				: DataResult.error(() -> "Not a tag ID: \"" + string + "\""),
			tag -> "#" + tag.location()
		);
	}

	public static <T> Codec<TagKey<T>> hashedTag(ResourceKey<? extends Registry<T>> registryRef) {
		return hashedTag(registryRef, ResourceLocation.DEFAULT_NAMESPACE);
	}

	public static <T> Codec<ResourceKey<T>> resourceKey(ResourceKey<? extends Registry<T>> registryRef, String defaultNamespace) {
		return DynamicResourceLocation.createCodec(defaultNamespace).xmap(location -> ResourceKey.create(registryRef, location), ResourceKey::location);
	}

	public static <T> Codec<ResourceKey<T>> resourceKey(ResourceKey<? extends Registry<T>> registryRef) {
		return resourceKey(registryRef, ResourceLocation.DEFAULT_NAMESPACE);
	}

}
