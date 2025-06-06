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

public record ItemCountNumberProvider() implements NumberProvider {

	public static final MapCodec<ItemCountNumberProvider> CODEC = MapCodec.unit(ItemCountNumberProvider::new);
	public static final PacketCodec<RegistryByteBuf, ItemCountNumberProvider> PACKET_CODEC = PacketCodec.unit(new ItemCountNumberProvider());

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ITEM_COUNT;
	}

	@Override
	public double doubleValue(Context context) {
		return context.required(ContextParameters.ITEM_STACK).getCount();
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.ITEM_STACK);
	}

}
