package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public record ContextKeyNumberProvider(TypedContextKey<Number> key) implements NumberProvider {

	public static final MapCodec<ContextKeyNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(NeoApoliCodecs.NUMBER_CONTEXT_KEY.fieldOf("key").forGetter(ContextKeyNumberProvider::key))
		.apply(instance, ContextKeyNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ContextKeyNumberProvider> STREAM_CODEC = StreamCodec.composite(
		NeoApoliStreamCodecs.NUMBER_CONTEXT_KEY, ContextKeyNumberProvider::key,
		ContextKeyNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.CONTEXT_KEY;
	}

	@Override
	public @NotNull Number next(Context context) {

		ResourceLocation id = key().name();
		Optional<Number> number = context.optional(key());

		if (number.isEmpty()) {
			context.getReporter().report("Couldn't get and provide number from parameter \"" + id + "\", as it's not included in the context!");
		}

		return number.orElse(0.0D);

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(key());
	}

}
