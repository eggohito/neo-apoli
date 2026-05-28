package io.github.eggohito.neo_apoli.provider.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.ConditionalValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliItemProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record ConditionalItemProvider(Condition condition, ItemProvider ifValue, ItemProvider elseValue) implements ItemProvider, ConditionalValueProvider<ItemProvider> {

	public static final MapCodec<ConditionalItemProvider> CODEC = MapCodecUtil.lazy(ConditionalItemProvider.class.getSimpleName(), () -> ConditionalValueProvider.mapCodec(ItemProvider.CODEC, ConditionalItemProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalItemProvider> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalItemProvider.class.getSimpleName(), () -> ConditionalValueProvider.streamCodec(ItemProvider.STREAM_CODEC, ConditionalItemProvider::new));

	@Override
	public ItemProvider.Type<?> getType() {
		return NeoApoliItemProviderTypes.CONDITIONAL;
	}

	@Override
	public ItemStack nextItem(Context context) {
		return this.nextOrElse(context, ItemProvider::nextItem, () -> ItemStack.EMPTY);
	}

}
