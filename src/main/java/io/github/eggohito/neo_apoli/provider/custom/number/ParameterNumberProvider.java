package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextParameter;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public record ParameterNumberProvider(TypedContextParameter<Number> parameter) implements NumberProvider {

	public static final MapCodec<ParameterNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(NeoApoliCodecs.NUMBER_PARAMETER.fieldOf("parameter").forGetter(ParameterNumberProvider::parameter))
		.apply(instance, ParameterNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, ParameterNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		NeoApoliPacketCodecs.NUMBER_PARAMETER, ParameterNumberProvider::parameter,
		ParameterNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.PARAMETER;
	}

	@Override
	public @NotNull Number next(Context context) {

		Identifier id = parameter().getId();
		Optional<Number> number = context.optional(parameter());

		if (number.isEmpty()) {
			context.getReporter().report("Couldn't get and provide number from parameter \"" + id + "\", as it's not included in the context!");
		}

		return number.orElse(0.0D);

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(parameter());
	}

}
