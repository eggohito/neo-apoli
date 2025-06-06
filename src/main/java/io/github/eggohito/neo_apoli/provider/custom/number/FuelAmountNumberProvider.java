package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public record FuelAmountNumberProvider() implements NumberProvider {

	public static final MapCodec<FuelAmountNumberProvider> CODEC = MapCodec.unit(FuelAmountNumberProvider::new);
	public static final PacketCodec<RegistryByteBuf, FuelAmountNumberProvider> PACKET_CODEC = PacketCodec.unit(new FuelAmountNumberProvider());

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.FUEL_AMOUNT;
	}

	@Override
	public double doubleValue(Context context) {
		ItemStack stack = context.required(ContextParameters.ITEM_STACK);
		return context.getWorld().getFuelRegistry().getFuelTicks(stack);
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.ITEM_STACK);
	}

}
