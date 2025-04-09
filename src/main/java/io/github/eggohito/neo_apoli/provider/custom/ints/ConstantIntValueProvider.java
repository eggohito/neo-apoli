package io.github.eggohito.neo_apoli.provider.custom.ints;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.IntValueProvider;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import io.github.eggohito.neo_apoli.provider.type.ints.IntValueProviderType;
import io.github.eggohito.neo_apoli.provider.type.ints.IntValueProviderTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record ConstantIntValueProvider(int value) implements IntValueProvider {

	public static final MapCodec<ConstantIntValueProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("value").forGetter(ConstantIntValueProvider::value)
	).apply(instance, ConstantIntValueProvider::new));

	public static final Codec<ConstantIntValueProvider> INLINE_CODEC = Codec.INT.xmap(ConstantIntValueProvider::new, ConstantIntValueProvider::value);
	public static final PacketCodec<RegistryByteBuf, ConstantIntValueProvider> PACKET_CODEC = PacketCodecs.INTEGER.xmap(ConstantIntValueProvider::new, ConstantIntValueProvider::value).cast();

	@Override
	public IntValueProviderType<?> getType() {
		return IntValueProviderTypes.CONSTANT;
	}

	@Override
	public int getInt(ValueProviderContext context) {
		return this.value();
	}

}
