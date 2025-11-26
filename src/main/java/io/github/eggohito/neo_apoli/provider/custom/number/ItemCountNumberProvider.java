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

public record ItemCountNumberProvider() implements NumberProvider {

	public static final MapCodec<ItemCountNumberProvider> CODEC = MapCodec.unit(ItemCountNumberProvider::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ItemCountNumberProvider> STREAM_CODEC = StreamCodecUtil.unit(ItemCountNumberProvider::new);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ITEM_COUNT;
	}

	@Override
	public @NotNull Number next(Context context) {
		return context.optional(NeoApoliContextKeys.ITEM_STACK)
			.map(ItemStack::getCount)
			.orElse(0);
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.ITEM_STACK);
	}

}
