package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.item.ItemProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record ItemCountMaxNumberProvider(ItemProvider item) implements NumberProvider {

	public static final MapCodec<ItemCountMaxNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(ItemProvider.CODEC.fieldOf("item").forGetter(ItemCountMaxNumberProvider::item))
		.apply(instance, ItemCountMaxNumberProvider::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ItemCountMaxNumberProvider> STREAM_CODEC = StreamCodec.composite(
		ItemProvider.STREAM_CODEC, ItemCountMaxNumberProvider::item,
		ItemCountMaxNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.ITEM_COUNT_MAX;
	}

	@Override
	public double getDouble(Context context) {

		Context itemContext = context.forChild(".item");
		ItemStack item = item().getItem(itemContext);

		if (itemContext.hasErrors()) {
			return 0;
		}

		else {
			return item.getMaxStackSize();
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		item().validate(validator.forChild(".item"));
	}

}
