package io.github.eggohito.neo_apoli.provider.custom.strings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.StringValueProvider;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import io.github.eggohito.neo_apoli.provider.type.strings.StringValueProviderType;
import io.github.eggohito.neo_apoli.provider.type.strings.StringValueProviderTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record ConstantStringValueProvider(String value) implements StringValueProvider {

	public static final MapCodec<ConstantStringValueProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("value").forGetter(ConstantStringValueProvider::value)
	).apply(instance, ConstantStringValueProvider::new));

	public static final Codec<ConstantStringValueProvider> INLINE_CODEC = Codec.STRING.xmap(ConstantStringValueProvider::new, ConstantStringValueProvider::value);
	public static final PacketCodec<RegistryByteBuf, ConstantStringValueProvider> PACKET_CODEC = PacketCodecs.STRING.xmap(ConstantStringValueProvider::new, ConstantStringValueProvider::value).cast();

	@Override
	public StringValueProviderType<?> getType() {
		return StringValueProviderTypes.CONSTANT;
	}

	@Override
	public String get(ValueProviderContext context) {
		return this.value();
	}

}
