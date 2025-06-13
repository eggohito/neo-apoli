package io.github.eggohito.neo_apoli.power.misc;

import org.jetbrains.annotations.NotNull;

public interface Prioritized<P extends Prioritized<P>> extends Comparable<P> {

	@Override
	default int compareTo(@NotNull P that) {
		return Integer.compare(this.getPriority(), that.getPriority());
	}

	int getPriority();

}
