package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.meta.ChoiceValueProvider;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record ChoiceNbtProvider(List<Case<NbtProvider>> cases, NbtProvider defaultValue) implements NbtProvider, ChoiceValueProvider<NbtProvider, Tag> {

	public static final MapCodec<ChoiceNbtProvider> CODEC = MapCodecUtil.lazy(ChoiceNbtProvider.class.getSimpleName(), () -> ChoiceValueProvider.createCodec(NbtProvider.CODEC, ChoiceNbtProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChoiceNbtProvider> STREAM_CODEC = StreamCodecUtil.lazy(ChoiceNbtProvider.class.getSimpleName(), () -> ChoiceValueProvider.createStreamCodec(NbtProvider.STREAM_CODEC, ChoiceNbtProvider::new));

	@Override
	public NbtProviderType<?> getType() {
		return NbtProviderTypes.CHOICE;
	}

}
