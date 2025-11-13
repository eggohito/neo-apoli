package io.github.eggohito.neo_apoli.provider.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;
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

			Context caseContext = context.makeChild(".cases[" + index + "]");
			boolean shouldProvide = aCase.condition().test(caseContext.makeChild(".condition"));

			if (!caseContext.hasErrors() && shouldProvide) {
				return aCase.value().next(caseContext.makeChild(".value"));
			}

		}

		return defaultValue().next(context.makeChild(".default"));

	}

	@Override
	default void validate(ErrorReporter reporter) {

		ValueProvider.super.validate(reporter);
		ListIterator<Case<P>> caseListIterator = cases().listIterator();

		while (caseListIterator.hasNext()) {

			int index = caseListIterator.nextIndex();
			Case<P> aCase = caseListIterator.next();

			aCase.validate(reporter.makeChild(".cases[" + index + "]"));

		}

	}

	static <P extends ValueProvider<V>, V, M extends ChoiceValueProvider<P, V>> MapCodec<M> codec(Codec<P> providerCodec, BiFunction<List<Case<P>>, P, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codecs.nonEmptyList(Case.codec(providerCodec).listOf()).fieldOf("cases").forGetter(ChoiceValueProvider::cases),
			providerCodec.fieldOf("default").forGetter(ChoiceValueProvider::defaultValue)
		).apply(instance, constructor));
	}

	static <P extends ValueProvider<V>, V, M extends ChoiceValueProvider<P, V>> PacketCodec<RegistryByteBuf, M> packetCodec(PacketCodec<RegistryByteBuf, P> providerCodec, BiFunction<List<Case<P>>, P, M> constructor) {
		return PacketCodec.tuple(
			PacketCodecs.collection(ObjectArrayList::new, Case.packetCodec(providerCodec)), ChoiceValueProvider::cases,
			providerCodec, ChoiceValueProvider::defaultValue,
			constructor
		);
	}

	record Case<P extends ValueProvider<?>>(Condition condition, P value) implements ContextAware {

		@Override
		public void validate(ErrorReporter reporter) {

			ContextAware.super.validate(reporter);

			condition().validate(reporter.makeChild(".condition"));
			value().validate(reporter.makeChild(".value"));

		}

		public static <P extends ValueProvider<?>> Codec<Case<P>> codec(Codec<P> codec) {
			return RecordCodecBuilder.create(instance -> instance.group(
				Condition.BASE_CODEC.fieldOf("condition").forGetter(Case::condition),
				codec.fieldOf("value").forGetter(Case::value)
			).apply(instance, Case::new));
		}

		public static <P extends ValueProvider<?>> PacketCodec<RegistryByteBuf, Case<P>> packetCodec(PacketCodec<RegistryByteBuf, P> codec) {
			return PacketCodec.tuple(
				Condition.BASE_PACKET_CODEC, Case::condition,
				codec, Case::value,
				Case::new
			);
		}

	}

}
