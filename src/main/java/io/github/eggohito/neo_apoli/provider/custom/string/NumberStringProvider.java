package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliStringProviderTypes;
import io.github.eggohito.neo_apoli.util.NumberType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record NumberStringProvider(NumberProvider number, NumberType as) implements StringProvider {

	public static final MapCodec<NumberStringProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("number").forGetter(NumberStringProvider::number),
		NumberType.CODEC.fieldOf("as").forGetter(NumberStringProvider::as)
	).apply(instance, NumberStringProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, NumberStringProvider> STREAM_CODEC = StreamCodec.composite(
		NumberProvider.STREAM_CODEC, NumberStringProvider::number,
		NumberType.STREAM_CODEC, NumberStringProvider::as,
		NumberStringProvider::new
	);

	@Override
	public @NotNull StringProvider.Type<?> getType() {
		return NeoApoliStringProviderTypes.NUMBER;
	}

	@Override
	public Optional<String> getString(Context context) {

		Context numberContext = context.forChild(".number");
		Number number = number().getAsType(as(), numberContext);

		return !numberContext.hasProblems()
			? Optional.of(number.toString())
			: Optional.empty();

	}

	@Override
	public void validate(Context.Validator validator) {
		StringProvider.super.validate(validator);
		number().validate(validator.forChild(".number"));
	}

}
