package io.github.eggohito.neo_apoli.hud;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;

import java.util.Optional;
import java.util.function.Function;

public interface NumberBoundHudElement extends HudElement {

	Codec<NumberBoundHudElement> CODEC = HudElement.CODEC.comapFlatMap(NumberBoundHudElement::validate, Function.identity());

	StreamCodec<RegistryFriendlyByteBuf, NumberBoundHudElement> STREAM_CODEC = HudElement.STREAM_CODEC.map(hudElement -> validate(hudElement).getOrThrow(), Function.identity());

	Optional<NumberProvider> value();

	Optional<NumberProvider> min();

	Optional<NumberProvider> max();

	@Override
	default void validate(Context.Validator validator) {

		HudElement.super.validate(validator);

		validateKeyAndField(validator, NeoApoliContextParams.CURRENT_VALUE, value(), "value");
		validateKeyAndField(validator, NeoApoliContextParams.MAX_VALUE, max(), "max");
		validateKeyAndField(validator, NeoApoliContextParams.MIN_VALUE, min(), "min");

	}

	static void validateKeyAndField(Context.Validator validator, ContextKey<?> key, Optional<NumberProvider> fieldMethod, String fieldName) {

		boolean keyIsAllowed = validator.keySet().allowed().contains(key);
		boolean fieldIsPresent = fieldMethod.isPresent();

		if (keyIsAllowed == fieldIsPresent) {
			validator.reportProblem("Either the parameter \"" + key.name() + "\" must be provided or the field \"" + fieldName + "\" be defined" + (fieldIsPresent ? ", not both" : "") + "!");
		}

	}

	static DataResult<NumberBoundHudElement> validate(HudElement hudElement) {
		return hudElement instanceof NumberBoundHudElement self
			? DataResult.success(self)
			: DataResult.error(() -> "Unknown number-bound HUD element: \"" + RegistryUtil.getId(NeoApoliRegistries.HUD_ELEMENT_TYPE, hudElement.getType()) + "\"");
	}

}
