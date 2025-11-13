package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.meta.ChoiceValueProvider;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Box;

import java.util.List;

public record ChoiceBoxProvider(List<Case<BoxProvider>> cases, BoxProvider defaultValue) implements BoxProvider, ChoiceValueProvider<BoxProvider, Box> {

	public static final MapCodec<ChoiceBoxProvider> CODEC = MapCodecUtil.lazy(ChoiceBoxProvider.class.getSimpleName(), () -> ChoiceValueProvider.codec(BoxProvider.CODEC, ChoiceBoxProvider::new));
	public static final PacketCodec<RegistryByteBuf, ChoiceBoxProvider> PACKET_CODEC = PacketCodecUtil.lazy(ChoiceBoxProvider.class.getSimpleName(), () -> ChoiceValueProvider.packetCodec(BoxProvider.PACKET_CODEC, ChoiceBoxProvider::new));

	@Override
	public BoxProviderType<?> getType() {
		return BoxProviderTypes.CHOICE;
	}

}
