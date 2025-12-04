package io.github.eggohito.neo_apoli.hud;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;

import java.util.Optional;
import java.util.function.Function;

public interface NumberBoundHudElement extends HudElement {

	Codec<NumberBoundHudElement> CODEC = HudElement.CODEC.comapFlatMap(NumberBoundHudElement::validate, Function.identity());

	StreamCodec<RegistryFriendlyByteBuf, NumberBoundHudElement> STREAM_CODEC = HudElement.STREAM_CODEC.map(hudElement -> validate(hudElement).getOrThrow(), Function.identity());

	Optional<NumberProvider> min();

	Optional<NumberProvider> max();

	Optional<NumberProvider> value();

	@Override
	default void validate(ProblemReporter reporter) {

		HudElement.super.validate(reporter);

		reportIfBothIsAbsent(reporter, NeoApoliContextKeys.MAX_VALUE, max(), "max");
		reportIfBothIsAbsent(reporter, NeoApoliContextKeys.MIN_VALUE, min(), "min");
		reportIfBothIsAbsent(reporter, NeoApoliContextKeys.CUR_VALUE, value(), "value");

	}

	static void reportIfBothIsAbsent(ProblemReporter reporter, ContextKey<?> key, Optional<NumberProvider> field, String name) {

		if (!reporter.getKeySet().allowed().contains(key) && field.isEmpty()) {
			reporter.report("Either the parameter \"" + key.name() + "\" must be provided, or the field \"" + name + "\" be defined!");
		}

	}

	static DataResult<NumberBoundHudElement> validate(HudElement hudElement) {
		return hudElement instanceof NumberBoundHudElement self
			? DataResult.success(self)
			: DataResult.error(() -> "Unknown number-bound HUD element: \"" + RegistryUtil.getId(NeoApoliRegistries.HUD_ELEMENT_TYPE, hudElement.getType()) + "\"");
	}

}
