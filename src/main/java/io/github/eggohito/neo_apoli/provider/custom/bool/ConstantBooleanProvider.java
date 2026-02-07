package io.github.eggohito.neo_apoli.provider.custom.bool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderType;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record ConstantBooleanProvider(boolean value) implements BooleanProvider {

	public static final MapCodec<ConstantBooleanProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.BOOL.fieldOf("value").forGetter(ConstantBooleanProvider::value)
	).apply(instance, ConstantBooleanProvider::new));

	public static final Codec<ConstantBooleanProvider> INLINE_CODEC = Codec.BOOL.xmap(
		ConstantBooleanProvider::new,
		ConstantBooleanProvider::value
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantBooleanProvider> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL, ConstantBooleanProvider::value,
		ConstantBooleanProvider::new
	);

	@Override
	public BooleanProviderType<?> getType() {
		return BooleanProviderTypes.CONSTANT;
	}

	@Override
	public @NotNull Boolean next(Context context) {
		return value();
	}

}
