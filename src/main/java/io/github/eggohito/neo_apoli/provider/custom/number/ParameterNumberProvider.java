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

public record ParameterNumberProvider(TypedContextKey<Number> parameter) implements NumberProvider {

	public static final MapCodec<ParameterNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(NeoApoliCodecs.NUMBER_CONTEXT_KEY.fieldOf("parameter").forGetter(ParameterNumberProvider::parameter))
		.apply(instance, ParameterNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ParameterNumberProvider> STREAM_CODEC = StreamCodec.composite(
		NeoApoliStreamCodecs.NUMBER_CONTEXT_KEY, ParameterNumberProvider::parameter,
		ParameterNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.PARAMETER;
	}

	@Override
	public @NotNull Number next(Context context) {

		ResourceLocation id = parameter().name();
		Optional<Number> number = context.optional(parameter());

		if (number.isEmpty()) {
			context.getReporter().report("Couldn't get and provide number from parameter \"" + id + "\", as it's not included in the context!");
		}

		return number.orElse(0.0D);

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(parameter());
	}

}
