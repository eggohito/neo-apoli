package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.meta.ChoiceValueProvider;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record ChoiceStringProvider(List<Case<StringProvider>> cases, StringProvider defaultValue) implements StringProvider, ChoiceValueProvider<StringProvider, String> {

	public static final MapCodec<ChoiceStringProvider> CODEC = MapCodecUtil.lazy(ChoiceStringProvider.class.getSimpleName(), () -> ChoiceValueProvider.codec(StringProvider.CODEC, ChoiceStringProvider::new));
	public static final PacketCodec<RegistryByteBuf, ChoiceStringProvider> PACKET_CODEC = PacketCodecUtil.lazy(ChoiceStringProvider.class.getSimpleName(), () -> ChoiceValueProvider.packetCodec(StringProvider.PACKET_CODEC, ChoiceStringProvider::new));

	@Override
	public StringProviderType<?> getType() {
		return StringProviderTypes.CHOICE;
	}

}
