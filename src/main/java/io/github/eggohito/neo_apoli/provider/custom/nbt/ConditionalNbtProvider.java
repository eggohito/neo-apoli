package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.ConditionalValueProvider;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record ConditionalNbtProvider(Condition condition, NbtProvider ifValue, NbtProvider elseValue) implements NbtProvider, ConditionalValueProvider<NbtProvider> {

	public static final MapCodec<ConditionalNbtProvider> MAP_CODEC = MapCodecUtil.lazy(ConditionalNbtProvider.class.getSimpleName(), () -> ConditionalValueProvider.mapCodec(NbtProvider.CODEC, ConditionalNbtProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalNbtProvider> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalNbtProvider.class.getSimpleName(), () -> ConditionalValueProvider.streamCodec(NbtProvider.STREAM_CODEC, ConditionalNbtProvider::new));

	@Override
	public @NotNull NbtProviderType<?> getType() {
		return NbtProviderTypes.CONDITIONAL;
	}

	@Override
	public @NotNull Tag nextTag(Context context) {
		return nextOrElse(context, NbtProvider::nextTag, CompoundTag::new);
	}

}
