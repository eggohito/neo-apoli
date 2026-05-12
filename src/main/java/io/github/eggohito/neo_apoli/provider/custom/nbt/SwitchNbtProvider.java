package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.SwitchValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNbtProviderTypes;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SwitchNbtProvider(List<Case<Condition, NbtProvider>> cases, NbtProvider defaultValue) implements NbtProvider, SwitchValueProvider<NbtProvider> {

	public static final MapCodec<SwitchNbtProvider> MAP_CODEC = MapCodecUtil.lazy(SwitchNbtProvider.class.getSimpleName(), () -> SwitchValueProvider.mapCodec(NbtProvider.CODEC, SwitchNbtProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SwitchNbtProvider> STREAM_CODEC = StreamCodecUtil.lazy(SwitchNbtProvider.class.getSimpleName(), () -> SwitchValueProvider.streamCodec(NbtProvider.STREAM_CODEC, SwitchNbtProvider::new));

	@Override
	public @NotNull NbtProvider.Type<?> getType() {
		return NeoApoliNbtProviderTypes.SWITCH;
	}

	@Override
	public @NotNull Tag nextTag(Context context) {
		return nextOrDefault(context, NbtProvider::nextTag);
	}

}
