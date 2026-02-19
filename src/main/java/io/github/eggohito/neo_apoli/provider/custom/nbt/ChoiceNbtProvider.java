package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.ChoiceValueProvider;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ChoiceNbtProvider(List<Case<Condition, NbtProvider>> cases, NbtProvider defaultValue) implements NbtProvider, ChoiceValueProvider<NbtProvider> {

	public static final MapCodec<ChoiceNbtProvider> MAP_CODEC = MapCodecUtil.lazy(ChoiceNbtProvider.class.getSimpleName(), () -> ChoiceValueProvider.mapCodec(NbtProvider.CODEC, ChoiceNbtProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChoiceNbtProvider> STREAM_CODEC = StreamCodecUtil.lazy(ChoiceNbtProvider.class.getSimpleName(), () -> ChoiceValueProvider.streamCodec(NbtProvider.STREAM_CODEC, ChoiceNbtProvider::new));

	@Override
	public @NotNull NbtProviderType<?> getType() {
		return NbtProviderTypes.CHOICE;
	}

	@Override
	public @NotNull Tag nextTag(Context context) {
		return nextOrDefault(context, NbtProvider::nextTag);
	}

}
