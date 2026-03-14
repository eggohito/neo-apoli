package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record ConstantNumberProvider(double value) implements NumberProvider {

	public static final MapCodec<ConstantNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.DOUBLE.fieldOf("value").forGetter(ConstantNumberProvider::value)
	).apply(instance, ConstantNumberProvider::new));

	public static final Codec<ConstantNumberProvider> INLINE_CODEC = Codec.DOUBLE.xmap(
		ConstantNumberProvider::new,
		ConstantNumberProvider::value
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantNumberProvider> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.DOUBLE, ConstantNumberProvider::value,
		ConstantNumberProvider::new
	);

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.CONSTANT;
	}

	@Override
	public double nextDouble(Context context) {
		return value();
	}

}
