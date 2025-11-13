package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.provider.custom.meta.ConditionalValueProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

public record ConditionalNumberProvider(Condition condition, NumberProvider ifValue, NumberProvider elseValue) implements NumberProvider, ConditionalValueProvider<NumberProvider, Number> {

	public static final MapCodec<ConditionalNumberProvider> CODEC = MapCodecUtil.lazy(ConditionalNumberProvider.class.getSimpleName(), () -> ConditionalValueProvider.codec(NumberProvider.CODEC, ConditionalNumberProvider::new));
	public static final PacketCodec<RegistryByteBuf, ConditionalNumberProvider> PACKET_CODEC = PacketCodecUtil.lazy(ConditionalNumberProvider.class.getSimpleName(), () -> ConditionalValueProvider.packetCodec(NumberProvider.PACKET_CODEC, ConditionalNumberProvider::new));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.CONDITIONAL;
	}

	@Override
	public @NotNull Number next(Context context) {
		return this.internalNextOrElse(context, () -> 0.0D);
	}

}
