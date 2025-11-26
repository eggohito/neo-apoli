package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record ItemCountMaxNumberProvider() implements NumberProvider {

	public static final MapCodec<ItemCountMaxNumberProvider> CODEC = MapCodec.unit(ItemCountMaxNumberProvider::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ItemCountMaxNumberProvider> STREAM_CODEC = StreamCodecUtil.unit(ItemCountMaxNumberProvider::new);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ITEM_COUNT_MAX;
	}

	@Override
	public @NotNull Number next(Context context) {
		return context.optional(NeoApoliContextKeys.ITEM_STACK)
			.map(ItemStack::getMaxStackSize)
			.orElse(0);
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.ITEM_STACK);
	}

}
