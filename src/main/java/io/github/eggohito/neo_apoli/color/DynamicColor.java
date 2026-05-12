package io.github.eggohito.neo_apoli.color;

import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.util.FloatFunction;
import io.github.eggohito.neo_apoli.util.FloatSupplier;

public interface DynamicColor extends Color {

	static float getValue(Context context, FloatFunction<Context> getter, FloatSupplier defaultValue) {
		float value = getter.apply(context);
		return context.hasErrors()
			? defaultValue.getAsFloat()
			: value;
	}

}
