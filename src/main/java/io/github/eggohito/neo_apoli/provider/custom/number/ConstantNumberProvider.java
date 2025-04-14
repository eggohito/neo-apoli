package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import io.github.eggohito.neo_apoli.provider.type.NumberProviderTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ConstantNumberProvider(Number value) implements NumberProvider {

	public static final MapCodec<ConstantNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.NUMBER.fieldOf("value").forGetter(ConstantNumberProvider::value)
	).apply(instance, ConstantNumberProvider::new));

	public static final Codec<ConstantNumberProvider> INLINE_CODEC = NeoApoliCodecs.NUMBER.xmap(
		ConstantNumberProvider::new,
		ConstantNumberProvider::value
	);

	public static final PacketCodec<RegistryByteBuf, ConstantNumberProvider> PACKET_CODEC = NeoApoliPacketCodecs.NUMBER.xmap(
		ConstantNumberProvider::new,
		ConstantNumberProvider::value
	).cast();

	@Override
	public Number get(ValueProviderContext context) {
		return this.value();
	}

	@Override
	public Type<?> getType() {
		return NumberProviderTypes.CONSTANT;
	}

}
