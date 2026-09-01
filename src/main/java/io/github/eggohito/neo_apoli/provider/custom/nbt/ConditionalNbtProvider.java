package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.ConditionalValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNbtProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ConditionalNbtProvider(Condition condition, NbtProvider onTrue, NbtProvider onFalse) implements NbtProvider, ConditionalValueProvider<NbtProvider> {

	public static final MapCodec<ConditionalNbtProvider> MAP_CODEC = MapCodecUtil.lazy(ConditionalNbtProvider.class.getSimpleName(), () -> ConditionalValueProvider.mapCodec(NbtProvider.CODEC, ConditionalNbtProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalNbtProvider> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalNbtProvider.class.getSimpleName(), () -> ConditionalValueProvider.streamCodec(NbtProvider.STREAM_CODEC, ConditionalNbtProvider::new));

	@Override
	public @NotNull NbtProvider.Type<?> getType() {
		return NeoApoliNbtProviderTypes.CONDITIONAL;
	}

	@Override
	public Optional<Tag> getTag(Context context) {
		return getValue(context, NbtProvider::getTag, Optional.empty());
	}

}
