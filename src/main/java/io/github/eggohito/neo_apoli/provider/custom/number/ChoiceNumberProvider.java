package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.ChoiceValueProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ChoiceNumberProvider(List<Case<Condition, NumberProvider>> cases, NumberProvider defaultValue) implements NumberProvider, ChoiceValueProvider<NumberProvider> {

	public static final MapCodec<ChoiceNumberProvider> MAP_CODEC = MapCodecUtil.lazy(ChoiceNumberProvider.class.getSimpleName(), () -> ChoiceValueProvider.mapCodec(NumberProvider.CODEC, ChoiceNumberProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChoiceNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(ChoiceNumberProvider.class.getSimpleName(), () -> ChoiceValueProvider.streamCodec(NumberProvider.STREAM_CODEC, ChoiceNumberProvider::new));

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.CHOICE;
	}

	@Override
	public @NotNull Number nextNumber(Context context) {
		return nextOrDefault(context, NumberProvider::nextNumber);
	}

}
