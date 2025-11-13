package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.meta.ChoiceValueProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record ChoiceNumberProvider(List<Case<NumberProvider>> cases, NumberProvider defaultValue) implements NumberProvider, ChoiceValueProvider<NumberProvider, Number> {

	public static final MapCodec<ChoiceNumberProvider> CODEC = MapCodecUtil.lazy(ChoiceNumberProvider.class.getSimpleName(), () -> ChoiceValueProvider.codec(NumberProvider.CODEC, ChoiceNumberProvider::new));
	public static final PacketCodec<RegistryByteBuf, ChoiceNumberProvider> PACKET_CODEC = PacketCodecUtil.lazy(ChoiceNumberProvider.class.getSimpleName(), () -> ChoiceValueProvider.packetCodec(NumberProvider.PACKET_CODEC, ChoiceNumberProvider::new));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.CHOICE;
	}

}
