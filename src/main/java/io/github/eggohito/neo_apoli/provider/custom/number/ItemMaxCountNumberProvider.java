package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public record ItemMaxCountNumberProvider() implements NumberProvider {

	public static final MapCodec<ItemMaxCountNumberProvider> CODEC = MapCodec.unit(ItemMaxCountNumberProvider::new);
	public static final PacketCodec<RegistryByteBuf, ItemMaxCountNumberProvider> PACKET_CODEC = PacketCodec.unit(new ItemMaxCountNumberProvider());

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ITEM_MAX_COUNT;
	}

	@Override
	public double doubleValue(Context context) {
		return context.required(ContextParameters.ITEM_STACK).getMaxCount();
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.ITEM_STACK);
	}

}
