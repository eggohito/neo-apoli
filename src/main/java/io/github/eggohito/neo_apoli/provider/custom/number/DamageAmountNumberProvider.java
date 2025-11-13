package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record DamageAmountNumberProvider() implements NumberProvider {

	public static final MapCodec<DamageAmountNumberProvider> CODEC = MapCodec.unit(DamageAmountNumberProvider::new);
	public static final PacketCodec<RegistryByteBuf, DamageAmountNumberProvider> PACKET_CODEC = PacketCodecUtil.unit(DamageAmountNumberProvider::new);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.DAMAGE_AMOUNT;
	}

	@Override
	public @NotNull Number next(Context context) {
		return context.optional(ContextParameters.DAMAGE_AMOUNT).orElse(0.0f);
	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(ContextParameters.DAMAGE_AMOUNT);
	}

}
