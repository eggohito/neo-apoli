package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.provider.custom.meta.ConditionalValueProvider;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

public record ConditionalStringProvider(Condition condition, StringProvider ifValue, StringProvider elseValue) implements StringProvider, ConditionalValueProvider<StringProvider, String> {

	public static final MapCodec<ConditionalStringProvider> CODEC = MapCodecUtil.lazy(ConditionalStringProvider.class.getSimpleName(), () -> ConditionalValueProvider.codec(StringProvider.CODEC, ConditionalStringProvider::new));
	public static final PacketCodec<RegistryByteBuf, ConditionalStringProvider> PACKET_CODEC = PacketCodecUtil.lazy(ConditionalStringProvider.class.getSimpleName(), () -> ConditionalValueProvider.packetCodec(StringProvider.PACKET_CODEC, ConditionalStringProvider::new));

	@Override
	public StringProviderType<?> getType() {
		return StringProviderTypes.CONDITIONAL;
	}

	@Override
	public @NotNull String next(Context context) {
		return internalNextOrElse(context, () -> "");
	}

}
