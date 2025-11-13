package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

public record ConstantNumberProvider(Number value) implements NumberProvider {

	public static final MapCodec<ConstantNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.NUMBER.fieldOf("value").forGetter(ConstantNumberProvider::value)
	).apply(instance, ConstantNumberProvider::new));

	public static final Codec<ConstantNumberProvider> INLINE_CODEC = NeoApoliCodecs.NUMBER.xmap(
		ConstantNumberProvider::new,
		ConstantNumberProvider::value
	);

	public static final PacketCodec<RegistryByteBuf, ConstantNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		NeoApoliPacketCodecs.NUMBER, ConstantNumberProvider::value,
		ConstantNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.CONSTANT;
	}

	@Override
	public @NotNull Number next(Context context) {
		return value();
	}

}
