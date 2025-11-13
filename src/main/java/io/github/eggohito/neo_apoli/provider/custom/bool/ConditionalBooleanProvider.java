package io.github.eggohito.neo_apoli.provider.custom.bool;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.provider.custom.meta.ConditionalValueProvider;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderType;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

public record ConditionalBooleanProvider(Condition condition, BooleanProvider ifValue, BooleanProvider elseValue) implements BooleanProvider, ConditionalValueProvider<BooleanProvider, Boolean> {

	public static final MapCodec<ConditionalBooleanProvider> CODEC = MapCodecUtil.lazy(ConditionalBooleanProvider.class.getSimpleName(), () -> ConditionalValueProvider.codec(BooleanProvider.CODEC, ConditionalBooleanProvider::new));
	public static final PacketCodec<RegistryByteBuf, ConditionalBooleanProvider> PACKET_CODEC = PacketCodecUtil.lazy(ConditionalBooleanProvider.class.getSimpleName(), () -> ConditionalValueProvider.packetCodec(BooleanProvider.PACKET_CODEC, ConditionalBooleanProvider::new));

	@Override
	public BooleanProviderType<?> getType() {
		return BooleanProviderTypes.CONDITIONAL;
	}

	@Override
	public @NotNull Boolean next(Context context) {
		return internalNextOrElse(context, () -> false);
	}

}
