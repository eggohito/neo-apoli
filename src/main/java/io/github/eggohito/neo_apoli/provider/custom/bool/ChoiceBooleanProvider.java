package io.github.eggohito.neo_apoli.provider.custom.bool;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.meta.ChoiceValueProvider;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderType;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record ChoiceBooleanProvider(List<Case<BooleanProvider>> cases, BooleanProvider defaultValue) implements BooleanProvider, ChoiceValueProvider<BooleanProvider, Boolean> {

	public static final MapCodec<ChoiceBooleanProvider> CODEC = MapCodecUtil.lazy(ChoiceBooleanProvider.class.getSimpleName(), () -> ChoiceValueProvider.createCodec(BooleanProvider.CODEC, ChoiceBooleanProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChoiceBooleanProvider> STREAM_CODEC = StreamCodecUtil.lazy(ChoiceBooleanProvider.class.getSimpleName(), () -> ChoiceValueProvider.createStreamCodec(BooleanProvider.STREAM_CODEC, ChoiceBooleanProvider::new));

	@Override
	public BooleanProviderType<?> getType() {
		return BooleanProviderTypes.CHOICE;
	}

}
