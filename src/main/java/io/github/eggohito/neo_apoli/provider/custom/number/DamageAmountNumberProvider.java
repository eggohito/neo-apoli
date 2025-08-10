package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

@EqualsAndHashCode
@Data
public final class DamageAmountNumberProvider extends NumberProvider {

	public static final MapCodec<DamageAmountNumberProvider> CODEC = MapCodec.unit(DamageAmountNumberProvider::new);
	public static final PacketCodec<RegistryByteBuf, DamageAmountNumberProvider> PACKET_CODEC = PacketCodec.unit(new DamageAmountNumberProvider());

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.DAMAGE_AMOUNT;
	}

	@Override
	protected Number impl(Context context) {
		return context.required(ContextParameters.DAMAGE_AMOUNT);
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.DAMAGE_AMOUNT);
	}

}
