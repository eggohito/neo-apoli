package io.github.eggohito.neo_apoli.codec;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.BaseMapCodec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public record FilteredUnboundedMapCodec<K, V>(Codec<K> keyCodec, Codec<V> elementCodec, Predicate<K> keyFilter) implements BaseMapCodec<K, V>, Codec<Map<K, V>> {

	@Override
	public <T> DataResult<Pair<Map<K, V>, T>> decode(DynamicOps<T> ops, T input) {
		return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap(mapInput -> filteredDecode(ops, mapInput)).map(parsedMap -> Pair.of(parsedMap, input));
	}

	@Override
	public <T> DataResult<T> encode(Map<K, V> input, DynamicOps<T> ops, T prefix) {
		return filteredEncode(input, ops, ops.mapBuilder()).build(prefix);
	}

	private <T> DataResult<Map<K, V>> filteredDecode(final DynamicOps<T> ops, final MapLike<T> mapInput) {

		Object2ObjectMap<K, V> read = new Object2ObjectArrayMap<>();
		Stream.Builder<Pair<T, T>> failed = Stream.builder();

		DataResult<Unit> result = mapInput.entries().reduce(
			DataResult.success(Unit.INSTANCE, Lifecycle.stable()),
			(r, pair) -> {

				DataResult<K> key = keyCodec().parse(ops, pair.getFirst());
				if (!key.resultOrPartial().map(k -> keyFilter().test(k)).orElse(true)) {
					return r;
				}

				DataResult<V> value = elementCodec().parse(ops, pair.getSecond());

				DataResult<Pair<K, V>> entryResult = key.apply2stable(Pair::of, value);
				Optional<Pair<K, V>> entry = entryResult.resultOrPartial();

				if (entry.isPresent()) {

					V existingValue = read.putIfAbsent(entry.get().getFirst(), entry.get().getSecond());

					if (existingValue != null) {
						failed.add(pair);
						return r.apply2stable((u, o) -> u, DataResult.error(() -> "Duplicate entry for key: '" + entry.get().getFirst() + "'"));
					}

				}

				if (entryResult.isError()) {
					failed.add(pair);
				}

				return r.apply2stable((u, p) -> u, entryResult);

			},
			(r1, r2) -> r1.apply2stable((u1, u2) -> u1, r2)
		);

		Map<K, V> elements = ImmutableMap.copyOf(read);
		T errors = ops.createMap(failed.build());

		return result.map(unit -> elements).setPartial(elements).mapError(e -> e + " missed input: " + errors);

	}

	private <T> RecordBuilder<T> filteredEncode(final Map<K, V> mapInput, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {

		for (final Map.Entry<K, V> entry : mapInput.entrySet()) {

			K key = entry.getKey();
			V value = entry.getValue();

			if (!keyFilter().test(key)) {
				prefix.add(keyCodec().encodeStart(ops, key), elementCodec().encodeStart(ops, value));
			}

		}

		return prefix;

	}

}
