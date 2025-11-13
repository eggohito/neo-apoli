package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.meta.ChoiceValueProvider;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record ChoiceNbtProvider(List<Case<NbtProvider>> cases, NbtProvider defaultValue) implements NbtProvider, ChoiceValueProvider<NbtProvider, NbtElement> {

	public static final MapCodec<ChoiceNbtProvider> CODEC = MapCodecUtil.lazy(ChoiceNbtProvider.class.getSimpleName(), () -> ChoiceValueProvider.codec(NbtProvider.CODEC, ChoiceNbtProvider::new));
	public static final PacketCodec<RegistryByteBuf, ChoiceNbtProvider> PACKET_CODEC = PacketCodecUtil.lazy(ChoiceNbtProvider.class.getSimpleName(), () -> ChoiceValueProvider.packetCodec(NbtProvider.PACKET_CODEC, ChoiceNbtProvider::new));

	@Override
	public NbtProviderType<?> getType() {
		return NbtProviderTypes.CHOICE;
	}

}
