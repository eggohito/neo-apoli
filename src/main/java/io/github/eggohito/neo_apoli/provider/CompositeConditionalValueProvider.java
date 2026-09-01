package io.github.eggohito.neo_apoli.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.conditional.CompositeConditional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiFunction;

public interface CompositeConditionalValueProvider<Provider extends ValueProvider> extends ValueProvider, CompositeConditional<Provider> {

	@NotNull
	default <Value> Value getOrDefault(Context context, BiFunction<Provider, Context, Value> getter) {

		var entriesIterator = entries().listIterator();

		while (entriesIterator.hasNext()) {

			Context entryContext = context.forChild(".entries[" + entriesIterator.nextIndex() + "]");
			var entry = entriesIterator.next();

			Context conditionContext = entryContext.forChild(".condition");
			boolean provides = entry.condition().test(conditionContext);

			if (!conditionContext.hasErrors() && provides) {
				return getter.apply(entry.value(), entryContext.forChild(".value"));
			}

		}

		return getter.apply(defaultValue(), context.forChild(".default"));

	}

	@Override
	default void validate(Context.Validator validator) {

		ValueProvider.super.validate(validator);

		MiscUtil.iterateList(
			entries(),
			(index, entry) -> {

				Context.Validator entryValidator = validator.forChild(".entries[" + index + "]");

				entry.condition().validate(entryValidator.forChild(".condition"));
				entry.value().validate(entryValidator.forChild(".value"));

			}
		);

		defaultValue().validate(validator.forChild(".default"));

	}

	static <P extends ValueProvider, M extends CompositeConditionalValueProvider<P>> MapCodec<M> mapCodec(Codec<P> providerCodec, BiFunction<List<CompositeConditional.Entry<P>>, P, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			ExtraCodecs.nonEmptyList(CompositeConditional.Entry.codec(Condition.CODEC, providerCodec).listOf()).fieldOf("entries").forGetter(CompositeConditionalValueProvider::entries),
			providerCodec.fieldOf("default").forGetter(CompositeConditionalValueProvider::defaultValue)
		).apply(instance, constructor));
	}

	static <P extends ValueProvider, M extends CompositeConditionalValueProvider<P>> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(StreamCodec<RegistryFriendlyByteBuf, P> providerCodec, BiFunction<List<CompositeConditional.Entry<P>>, P, M> constructor) {
		return StreamCodec.composite(
			CompositeConditional.Entry.streamCodec(Condition.STREAM_CODEC, providerCodec).apply(ByteBufCodecs.list()), CompositeConditionalValueProvider::entries,
			providerCodec, CompositeConditionalValueProvider::defaultValue,
			constructor
		);
	}

}
