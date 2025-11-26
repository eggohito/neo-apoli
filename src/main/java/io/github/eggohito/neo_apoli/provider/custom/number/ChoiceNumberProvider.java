package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.meta.ChoiceValueProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record ChoiceNumberProvider(List<Case<NumberProvider>> cases, NumberProvider defaultValue) implements NumberProvider, ChoiceValueProvider<NumberProvider, Number> {

	public static final MapCodec<ChoiceNumberProvider> CODEC = MapCodecUtil.lazy(ChoiceNumberProvider.class.getSimpleName(), () -> ChoiceValueProvider.createCodec(NumberProvider.CODEC, ChoiceNumberProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChoiceNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(ChoiceNumberProvider.class.getSimpleName(), () -> ChoiceValueProvider.createStreamCodec(NumberProvider.STREAM_CODEC, ChoiceNumberProvider::new));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.CHOICE;
	}

}
