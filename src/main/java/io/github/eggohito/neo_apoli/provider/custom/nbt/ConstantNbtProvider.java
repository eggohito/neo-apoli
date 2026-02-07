package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record ConstantNbtProvider(Tag value) implements NbtProvider {

	public static final MapCodec<ConstantNbtProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.REGULAR_OR_STRINGIFIED_TAG.fieldOf("value").forGetter(ConstantNbtProvider::value)
	).apply(instance, ConstantNbtProvider::new));

	public static final Codec<ConstantNbtProvider> INLINE_CODEC = NeoApoliCodecs.REGULAR_OR_STRINGIFIED_TAG.xmap(
		ConstantNbtProvider::new,
		ConstantNbtProvider::value
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantNbtProvider> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.TRUSTED_TAG, ConstantNbtProvider::value,
		ConstantNbtProvider::new
	);

	@Override
	public NbtProviderType<?> getType() {
		return NbtProviderTypes.CONSTANT;
	}

	@Override
	public @NotNull Tag next(Context context) {
		return value();
	}

}
