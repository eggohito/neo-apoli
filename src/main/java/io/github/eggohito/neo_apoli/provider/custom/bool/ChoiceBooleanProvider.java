package io.github.eggohito.neo_apoli.provider.custom.bool;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.meta.ChoiceValueProvider;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderType;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record ChoiceBooleanProvider(List<Case<BooleanProvider>> cases, BooleanProvider defaultValue) implements BooleanProvider, ChoiceValueProvider<BooleanProvider, Boolean> {

	public static final MapCodec<ChoiceBooleanProvider> CODEC = MapCodecUtil.lazy(ChoiceBooleanProvider.class.getSimpleName(), () -> ChoiceValueProvider.codec(BooleanProvider.CODEC, ChoiceBooleanProvider::new));
	public static final PacketCodec<RegistryByteBuf, ChoiceBooleanProvider> PACKET_CODEC = PacketCodecUtil.lazy(ChoiceBooleanProvider.class.getSimpleName(), () -> ChoiceValueProvider.packetCodec(BooleanProvider.PACKET_CODEC, ChoiceBooleanProvider::new));

	@Override
	public BooleanProviderType<?> getType() {
		return BooleanProviderTypes.CHOICE;
	}

}
