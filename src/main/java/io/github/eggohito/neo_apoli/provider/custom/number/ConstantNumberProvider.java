package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
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
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.CONSTANT;
	}

	@Override
	public double doubleValue(Context context) {
		return value().doubleValue();
	}

	@Override
	public long longValue(Context context) {
		return value().longValue();
	}

}
