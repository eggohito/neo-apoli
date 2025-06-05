package io.github.eggohito.neo_apoli.provider.misc;

import io.github.eggohito.neo_apoli.provider.StringProvider;

import java.util.List;
import java.util.ListIterator;
import java.util.function.BiConsumer;

public interface MultiStringProvider extends StringProvider {

	List<StringProvider> strings();

	@Override
	default void validate(ErrorReporter reporter) {
		this.iterate((index, string) -> string.validate(reporter.makeChild("strings[" + index + "]")));
	}

	default void iterate(BiConsumer<Integer, StringProvider> processor) {

		ListIterator<StringProvider> stringIterator = strings().listIterator();

		while (stringIterator.hasNext()) {
			processor.accept(stringIterator.nextIndex(), stringIterator.next());
		}

	}

}
