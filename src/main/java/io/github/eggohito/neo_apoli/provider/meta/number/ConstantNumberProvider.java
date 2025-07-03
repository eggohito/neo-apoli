package io.github.eggohito.neo_apoli.provider.meta.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class ConstantNumberProvider extends NumberProvider {

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

	private final Number value;

	public ConstantNumberProvider(Number value) {
		this.value = value;
	}

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.CONSTANT;
	}

	@Override
	protected Number impl(Context context) {
		return value();
	}

}
