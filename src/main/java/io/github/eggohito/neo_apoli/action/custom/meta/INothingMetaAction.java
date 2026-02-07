package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.*;
import io.github.eggohito.neo_apoli.context.Context;

import java.util.function.Supplier;
import java.util.stream.Stream;

public interface INothingMetaAction extends MetaAction {

	@Override
	default void execute(Context context) {

	}

	@Override
	default void validate(Context.Validator validator) {

	}

	static <M extends INothingMetaAction> MapCodec<M> createEmptyInputMapCodec(Supplier<M> constructor) {
		return new MapCodec<>() {

			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return Stream.empty();
			}

			@Override
			public <T> DataResult<M> decode(DynamicOps<T> ops, MapLike<T> input) {

				long fieldCount = input
					.entries()
					.count();

				if (fieldCount == 0) {
					return DataResult.success(constructor.get());
				}

				else {
					return DataResult.error(() -> "Couldn't consider input as empty, as it has " + fieldCount + " field(s)!");
				}

			}

			@Override
			public <T> RecordBuilder<T> encode(M input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
				return prefix;
			}

		};
	}

	static <M extends INothingMetaAction> Codec<M> createEmptyInputCodec(Supplier<M> constructor) {
		return createEmptyInputMapCodec(constructor).codec();
	}

}
