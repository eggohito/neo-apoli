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

public record ItemCountNumberProvider(ItemProvider item) implements NumberProvider {

	public static final MapCodec<ItemCountNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(ItemProvider.CODEC.fieldOf("item").forGetter(ItemCountNumberProvider::item))
		.apply(instance, ItemCountNumberProvider::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ItemCountNumberProvider> STREAM_CODEC = StreamCodec.composite(
		ItemProvider.STREAM_CODEC, ItemCountNumberProvider::item,
		ItemCountNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.ITEM_COUNT;
	}

	@Override
	public double getDouble(Context context) {

		Context itemContext = context.forChild(".item");
		ItemStack item = item().getItem(itemContext);

		if (itemContext.hasProblems()) {
			return 0;
		}

		else {
			return item.getCount();
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		item().validate(validator.forChild(".item"));
	}

}
