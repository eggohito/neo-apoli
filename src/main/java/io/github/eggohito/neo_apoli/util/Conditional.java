package io.github.eggohito.neo_apoli.util;

import io.github.eggohito.neo_apoli.condition.Condition;

public interface Conditional<T> {

	Condition condition();

	T onTrue();

	T onFalse();

}
