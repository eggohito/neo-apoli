package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextValidatable;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface MultiNumberProvider extends NumberProvider {

	List<NumberProvider> numbers();

	@Override
	default void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		ContextValidatable.validate(numbers(), validator, index -> ".numbers[" + index + "]");
	}

	default <N extends Number> N iterateAndProcess(Context context, BiFunction<NumberProvider, Context, N> getter, BiFunction<N, N, N> processor, N initialValue) {

		MutableObject<N> result = new MutableObject<>(initialValue);
		MutableBoolean init = new MutableBoolean(false);

		MiscUtil.iterateList(
			numbers(),
			(index, number) -> {

				Context numberContext = context.forChild(".numbers[" + index + "]");
				N value = getter.apply(number, numberContext);

				try {

					if (numberContext.visitor().push(number) && !numberContext.hasProblems()) {

						if (init.isTrue()) {
							result.setValue(processor.apply(result.getValue(), value));
						}

						else {
							result.setValue(value);
						}

					}

					else {
						numberContext.reportProblem("Number provider was invoked recursively!");
					}

				}

				finally {
					numberContext.visitor().pop(number);
				}

				init.setTrue();

			}
		);

		return result.getValue();

	}

	static <M extends MultiNumberProvider> MapCodec<M> codec(Function<List<NumberProvider>, M> constructor) {
		return MapCodecUtil.lazy(() -> RecordCodecBuilder.mapCodec(instance -> instance.group(
			NumberProvider.CODEC.listOf().fieldOf("numbers").forGetter(MultiNumberProvider::numbers)
		).apply(instance, constructor)));
	}

	static <M extends MultiNumberProvider> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(Function<List<NumberProvider>, M> constructor) {
		return StreamCodecUtil.lazy(() -> StreamCodec.composite(
			ByteBufCodecs.collection(ObjectArrayList::new, NumberProvider.STREAM_CODEC), MultiNumberProvider::numbers,
			constructor
		));
	}

}
