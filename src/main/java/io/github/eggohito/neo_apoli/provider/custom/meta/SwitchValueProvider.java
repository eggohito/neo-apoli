package io.github.eggohito.neo_apoli.provider.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ListIterator;
import java.util.function.BiFunction;

public interface SwitchValueProvider<P extends ValueProvider> extends ValueProvider {

	List<Case<Condition, P>> cases();

	P defaultValue();

	@NotNull
	default <V> V nextOrDefault(Context context, BiFunction<P, Context, V> getter) {

		ListIterator<Case<Condition, P>> listIterator = cases().listIterator();

		while (listIterator.hasNext()) {

			Context caseContext = context.forChild(".cases[" + listIterator.nextIndex() + "]");
			Case<Condition, P> aCase = listIterator.next();

			Context conditionContext = caseContext.forChild(".condition");
			boolean provides = aCase.condition().test(conditionContext);

			if (!conditionContext.hasErrors() && provides) {
				return getter.apply(aCase.value(), caseContext.forChild(".value"));
			}

		}

		return getter.apply(defaultValue(), context.forChild(".default"));

	}

	@Override
	default void validate(Context.Validator validator) {

		ValueProvider.super.validate(validator);

		MiscUtil.iterateList(
			cases(),
			(index, aCase) -> {

				Context.Validator caseValidator = validator.forChild(".cases[" + index + "]");

				aCase.condition().validate(caseValidator.forChild(".condition"));
				aCase.value().validate(caseValidator.forChild(".value"));

			}
		);

		defaultValue().validate(validator.forChild(".default"));

	}

	static <P extends ValueProvider, M extends SwitchValueProvider<P>> MapCodec<M> mapCodec(Codec<P> providerCodec, BiFunction<List<Case<Condition, P>>, P, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			ExtraCodecs.nonEmptyList(Case.codec(Condition.CODEC, providerCodec).listOf()).fieldOf("cases").forGetter(SwitchValueProvider::cases),
			providerCodec.fieldOf("default").forGetter(SwitchValueProvider::defaultValue)
		).apply(instance, constructor));
	}

	static <P extends ValueProvider, M extends SwitchValueProvider<P>> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(StreamCodec<RegistryFriendlyByteBuf, P> providerCodec, BiFunction<List<Case<Condition, P>>, P, M> constructor) {
		return StreamCodec.composite(
			Case.streamCodec(Condition.STREAM_CODEC, providerCodec).apply(ByteBufCodecs.list()), SwitchValueProvider::cases,
			providerCodec, SwitchValueProvider::defaultValue,
			constructor
		);
	}

}
