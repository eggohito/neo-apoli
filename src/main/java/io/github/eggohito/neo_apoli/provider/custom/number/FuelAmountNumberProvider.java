package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import net.minecraft.item.FuelRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record FuelAmountNumberProvider() implements NumberProvider {

	public static final MapCodec<FuelAmountNumberProvider> CODEC = MapCodec.unit(FuelAmountNumberProvider::new);
	public static final PacketCodec<RegistryByteBuf, FuelAmountNumberProvider> PACKET_CODEC = PacketCodecUtil.unit(FuelAmountNumberProvider::new);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.FUEL_AMOUNT;
	}

	@Override
	public @NotNull Number next(Context context) {
		FuelRegistry fuelRegistry = context.getWorld().getFuelRegistry();
		return context.optional(NeoApoliContextParameters.ITEM_STACK)
			.map(fuelRegistry::getFuelTicks)
			.orElse(0);
	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParameters.ITEM_STACK);
	}

}
