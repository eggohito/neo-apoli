package io.github.eggohito.neo_apoli.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.github.eggohito.neo_apoli.util.DynamicResourceLocation;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public final class ValueSuppliedElementCodec<E> implements Codec<E> {

	private final Codec<E> elementCodec;
	private final boolean allowInlineDefinitions;

	private final Function<ResourceLocation, DataResult<E>> elementGetter;
	private final Function<E, DataResult<ResourceLocation>> idGetter;

	public ValueSuppliedElementCodec(Codec<E> elementCodec, boolean allowInlineDefinitions, Function<ResourceLocation, DataResult<E>> elementGetter, Function<E, DataResult<ResourceLocation>> idGetter) {
		this.elementCodec = elementCodec;
		this.allowInlineDefinitions = allowInlineDefinitions;
		this.elementGetter = elementGetter;
		this.idGetter = idGetter;
	}

	@Override
	public <I> DataResult<Pair<E, I>> decode(DynamicOps<I> ops, I input) {
		return switch (DynamicResourceLocation.CODEC.parse(ops, input)) {
			case DataResult.Success<ResourceLocation> success ->
				elementGetter.apply(success.value()).map(e -> Pair.of(e, input));
			case DataResult.Error<ResourceLocation> ignored -> {

				if (allowInlineDefinitions) {
					yield elementCodec.decode(ops, input);
				}

				else {
					yield DataResult.error(() -> "Inline definitions are not allowed here!");
				}

			}
		};
	}

	@Override
	public <I> DataResult<I> encode(E value, DynamicOps<I> ops, I prefix) {
		return idGetter.apply(value).mapOrElse(id -> DynamicResourceLocation.CODEC.encode(id, ops, prefix), error -> elementCodec.encode(value, ops, prefix));
	}

	public boolean allowInlineDefinitions() {
		return allowInlineDefinitions;
	}

}
