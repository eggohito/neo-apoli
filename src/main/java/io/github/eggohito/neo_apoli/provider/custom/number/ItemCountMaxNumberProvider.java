package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public enum ItemCountMaxNumberProvider implements NumberProvider {

	INSTANCE;

	public static final MapCodec<ItemCountMaxNumberProvider> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, ItemCountMaxNumberProvider> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.ITEM_COUNT_MAX;
	}

	@Override
	public double nextDouble(Context context) {
		return context.getOptional(NeoApoliContextParams.ITEM_STACK)
			.map(ItemStack::getMaxStackSize)
			.orElse(0);
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.ITEM_STACK);
	}

}
