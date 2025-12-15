package io.github.eggohito.neo_apoli.provider.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ListIterator;
import java.util.function.BiFunction;

public interface ChoiceValueProvider<P extends ValueProvider<V>, V> extends ValueProvider<V> {

	List<Case<P>> cases();

	P defaultValue();

	@Override
	@NotNull
	default V next(Context context) {

		ListIterator<Case<P>> caseListIterator = cases().listIterator();
		while (caseListIterator.hasNext()) {

			int index = caseListIterator.nextIndex();
			Case<P> aCase = caseListIterator.next();

			Context caseContext = context.forChild(".cases[" + index + "]");
			boolean shouldProvide = aCase.condition().test(caseContext.forChild(".condition"));

			if (!caseContext.hasErrors() && shouldProvide) {
				return aCase.value().next(caseContext.forChild(".value"));
			}

		}

		return defaultValue().next(context.forChild(".default"));

	}

	@Override
	default void validate(ProblemReporter reporter) {

		ValueProvider.super.validate(reporter);
		ListIterator<Case<P>> caseListIterator = cases().listIterator();

		while (caseListIterator.hasNext()) {

			int index = caseListIterator.nextIndex();
			Case<P> aCase = caseListIterator.next();

			aCase.validate(reporter.forChild(".cases[" + index + "]"));

		}

	}

	static <P extends ValueProvider<V>, V, M extends ChoiceValueProvider<P, V>> MapCodec<M> createCodec(Codec<P> providerCodec, BiFunction<List<Case<P>>, P, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			ExtraCodecs.nonEmptyList(Case.createCodec(providerCodec).listOf()).fieldOf("cases").forGetter(ChoiceValueProvider::cases),
			providerCodec.fieldOf("default").forGetter(ChoiceValueProvider::defaultValue)
		).apply(instance, constructor));
	}

	static <P extends ValueProvider<V>, V, M extends ChoiceValueProvider<P, V>> StreamCodec<RegistryFriendlyByteBuf, M> createStreamCodec(StreamCodec<RegistryFriendlyByteBuf, P> providerCodec, BiFunction<List<Case<P>>, P, M> constructor) {
		return StreamCodec.composite(
			ByteBufCodecs.collection(ObjectArrayList::new, Case.createStreamCodec(providerCodec)), ChoiceValueProvider::cases,
			providerCodec, ChoiceValueProvider::defaultValue,
			constructor
		);
	}

	record Case<P extends ValueProvider<?>>(Condition condition, P value) implements ContextAware {

		@Override
		public void validate(ProblemReporter reporter) {

			ContextAware.super.validate(reporter);

			condition().validate(reporter.forChild(".condition"));
			value().validate(reporter.forChild(".value"));

		}

		public static <P extends ValueProvider<?>> Codec<Case<P>> createCodec(Codec<P> codec) {
			return RecordCodecBuilder.create(instance -> instance.group(
				Condition.CODEC.fieldOf("condition").forGetter(Case::condition),
				codec.fieldOf("value").forGetter(Case::value)
			).apply(instance, Case::new));
		}

		public static <P extends ValueProvider<?>> StreamCodec<RegistryFriendlyByteBuf, Case<P>> createStreamCodec(StreamCodec<RegistryFriendlyByteBuf, P> codec) {
			return StreamCodec.composite(
				Condition.STREAM_CODEC, Case::condition,
				codec, Case::value,
				Case::new
			);
		}

	}

}
