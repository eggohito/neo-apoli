package io.github.eggohito.neo_apoli.util;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;

public interface FloatBiFunction {

	float apply(float first, float second);

	default FloatBiFunction andThen(FloatUnaryOperator after) {
		return (first, second) -> after.apply(this.apply(first, second));
	}

}
