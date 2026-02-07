package io.github.eggohito.neo_apoli.util.color.dynamic;

import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.util.FloatFunction;
import io.github.eggohito.neo_apoli.util.FloatSupplier;
import io.github.eggohito.neo_apoli.util.color.Color;

public interface DynamicColor extends Color {

	static float getValue(Context context, FloatFunction<Context> getter, FloatSupplier defaultValue) {
		float value = getter.apply(context);
		return context.hasErrors()
			? defaultValue.getAsFloat()
			: value;
	}

}
