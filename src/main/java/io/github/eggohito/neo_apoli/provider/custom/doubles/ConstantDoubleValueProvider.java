package io.github.eggohito.neo_apoli.provider.custom.doubles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.DoubleValueProvider;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import io.github.eggohito.neo_apoli.provider.type.doubles.DoubleValueProviderType;
import io.github.eggohito.neo_apoli.provider.type.doubles.DoubleValueProviderTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record ConstantDoubleValueProvider(double value) implements DoubleValueProvider {

	public static final MapCodec<ConstantDoubleValueProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.DOUBLE.fieldOf("value").forGetter(ConstantDoubleValueProvider::value)
	).apply(instance, ConstantDoubleValueProvider::new));

	public static final Codec<ConstantDoubleValueProvider> INLINE_CODEC = Codec.DOUBLE.xmap(ConstantDoubleValueProvider::new, ConstantDoubleValueProvider::value);
	public static final PacketCodec<RegistryByteBuf, ConstantDoubleValueProvider> PACKET_CODEC = PacketCodecs.DOUBLE.xmap(ConstantDoubleValueProvider::new, ConstantDoubleValueProvider::value).cast();

	@Override
	public double getDouble(ValueProviderContext context) {
		return this.value();
	}

	@Override
	public DoubleValueProviderType<?> getType() {
		return DoubleValueProviderTypes.CONSTANT;
	}

}
