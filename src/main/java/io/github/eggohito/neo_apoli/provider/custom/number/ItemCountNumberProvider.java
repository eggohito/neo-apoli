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

public enum ItemCountNumberProvider implements NumberProvider {

	INSTANCE;

	public static final MapCodec<ItemCountNumberProvider> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, ItemCountNumberProvider> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.ITEM_COUNT;
	}

	@Override
	public double nextDouble(Context context) {
		return context.getOptional(NeoApoliContextParams.ITEM_STACK)
			.map(ItemStack::getCount)
			.orElse(0);
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.ITEM_STACK);
	}

}
