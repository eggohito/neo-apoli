package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record ItemCountMaxNumberProvider() implements NumberProvider {

	public static final MapCodec<ItemCountMaxNumberProvider> CODEC = MapCodec.unit(ItemCountMaxNumberProvider::new);
	public static final PacketCodec<RegistryByteBuf, ItemCountMaxNumberProvider> PACKET_CODEC = PacketCodecUtil.unit(ItemCountMaxNumberProvider::new);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ITEM_COUNT_MAX;
	}

	@Override
	public @NotNull Number next(Context context) {
		return context.optional(NeoApoliContextParameters.ITEM_STACK)
			.map(ItemStack::getMaxCount)
			.orElse(0);
	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParameters.ITEM_STACK);
	}

}
