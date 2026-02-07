package io.github.eggohito.neo_apoli.context.parameter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.CommandParameter;
import io.github.eggohito.neo_apoli.util.Typed;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class ContextParameter<T> extends ContextKey<T> implements Typed<T>, CommandParameter {

	public ContextParameter(ResourceLocation name) {
		super(name);
	}

	public boolean checkTypeClass(Predicate<Class<T>> tester) {
		Class<T> typeClass = this.typeClass();
		return typeClass != null
			&& tester.test(typeClass);
	}

	public static <T> Codec<ContextParameter<T>> codec(String name, Class<T> typeClass) {
		return NeoApoliContextParams.CODEC.comapFlatMap(validator(name, typeClass), Function.identity());
	}

	public static <T> StreamCodec<RegistryFriendlyByteBuf, ContextParameter<T>> streamCodec(String name, Class<T> typeClass) {
		return NeoApoliContextParams.STREAM_CODEC.map(validator(name, typeClass).andThen(DataResult::getOrThrow), Function.identity());
	}

	@SuppressWarnings("unchecked")
	public static <T> Function<ContextParameter<?>, DataResult<ContextParameter<T>>> validator(String name, Class<T> typeClass) {
		return key -> {

			if (key.checkTypeClass(typeClass::isAssignableFrom)) {
				return DataResult.success((ContextParameter<T>) key);
			}

			else {
				return DataResult.error(() -> "Unknown " + name.toLowerCase(Locale.ROOT) + " parameter: \"" + key.name() + "\"");
			}

		};
	}

}
