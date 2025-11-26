package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.meta.ChoiceValueProvider;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record ChoiceStringProvider(List<Case<StringProvider>> cases, StringProvider defaultValue) implements StringProvider, ChoiceValueProvider<StringProvider, String> {

	public static final MapCodec<ChoiceStringProvider> CODEC = MapCodecUtil.lazy(ChoiceStringProvider.class.getSimpleName(), () -> ChoiceValueProvider.createCodec(StringProvider.CODEC, ChoiceStringProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChoiceStringProvider> STREAM_CODEC = StreamCodecUtil.lazy(ChoiceStringProvider.class.getSimpleName(), () -> ChoiceValueProvider.createStreamCodec(StringProvider.STREAM_CODEC, ChoiceStringProvider::new));

	@Override
	public StringProviderType<?> getType() {
		return StringProviderTypes.CHOICE;
	}

}
