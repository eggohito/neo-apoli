package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public record ContextNumberProvider(Context.Parameter<Number> parameter) implements NumberProvider {

	public static final MapCodec<ContextNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(NeoApoliContextParams.Codecs.NUMBER.fieldOf("parameter").forGetter(ContextNumberProvider::parameter))
		.apply(instance, ContextNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ContextNumberProvider> STREAM_CODEC = StreamCodec.composite(
		NeoApoliContextParams.StreamCodecs.NUMBER, ContextNumberProvider::parameter,
		ContextNumberProvider::new
	);

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.CONTEXT;
	}

	@Override
	public double nextDouble(Context context) {

		ResourceLocation id = parameter().name();
		Optional<Number> number = context.getOptional(parameter());

		if (number.isEmpty()) {
			context.reportProblem("Couldn't get and provide number from parameter \"" + id + "\"; it's not included in the context!");
		}

		return number
			.map(Number::doubleValue)
			.orElse(0.0D);

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(parameter());
	}

}
