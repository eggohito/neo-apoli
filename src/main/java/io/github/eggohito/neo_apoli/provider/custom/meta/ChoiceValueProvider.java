package io.github.eggohito.neo_apoli.provider.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextUser;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
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

		ListIterator<Case<P>> listIterator = cases().listIterator();

		while (listIterator.hasNext()) {

			Context caseContext = context.forChild(".cases[" + listIterator.nextIndex() + "]");
			Case<P> aCase = listIterator.next();

			Context conditionContext = caseContext.forChild(".condition");
			boolean provides = aCase.condition().test(conditionContext);

			if (!conditionContext.hasErrors() && provides) {
				return aCase.value().next(caseContext.forChild(".value"));
			}

		}

		return defaultValue().next(context.forChild(".default"));

	}

	@Override
	default void validate(Context.Validator validator) {

		ValueProvider.super.validate(validator);
		ListIterator<Case<P>> listIterator = cases().listIterator();

		while (listIterator.hasNext()) {

			Context.Validator caseValidator = validator.forChild(".cases[" + listIterator.nextIndex() + "]");

			listIterator.next().validate(caseValidator);

		}

	}

	static <P extends ValueProvider<V>, V, M extends ChoiceValueProvider<P, V>> MapCodec<M> mapCodec(Codec<P> providerCodec, BiFunction<List<Case<P>>, P, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			ExtraCodecs.nonEmptyList(Case.createCodec(providerCodec).listOf()).fieldOf("cases").forGetter(ChoiceValueProvider::cases),
			providerCodec.fieldOf("default").forGetter(ChoiceValueProvider::defaultValue)
		).apply(instance, constructor));
	}

	static <P extends ValueProvider<V>, V, M extends ChoiceValueProvider<P, V>> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(StreamCodec<RegistryFriendlyByteBuf, P> providerCodec, BiFunction<List<Case<P>>, P, M> constructor) {
		return StreamCodec.composite(
			ByteBufCodecs.collection(ObjectArrayList::new, Case.createStreamCodec(providerCodec)), ChoiceValueProvider::cases,
			providerCodec, ChoiceValueProvider::defaultValue,
			constructor
		);
	}

	record Case<P extends ValueProvider<?>>(Condition condition, P value) implements ContextUser {

		@Override
		public void validate(Context.Validator validator) {

			ContextUser.super.validate(validator);

			condition().validate(validator.forChild(".condition"));
			value().validate(validator.forChild(".value"));

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
