package io.github.eggohito.neo_apoli.provider.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.SwitchValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliItemProviderTypes;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SwitchItemProvider(List<Case<Condition, ItemProvider>> cases, ItemProvider defaultValue) implements ItemProvider, SwitchValueProvider<ItemProvider> {

	public static final MapCodec<SwitchItemProvider> CODEC = MapCodecUtil.lazy(SwitchItemProvider.class.getSimpleName(), () -> SwitchValueProvider.mapCodec(ItemProvider.CODEC, SwitchItemProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SwitchItemProvider> STREAM_CODEC = StreamCodecUtil.lazy(SwitchItemProvider.class.getSimpleName(), () -> SwitchValueProvider.streamCodec(ItemProvider.STREAM_CODEC, SwitchItemProvider::new));

	@Override
	public ItemProvider.@NotNull Type<?> getType() {
		return NeoApoliItemProviderTypes.SWITCH;
	}

	@Override
	public @NotNull ItemStack getItem(Context context) {
		return this.getOrDefault(context, ItemProvider::getItem);
	}

}
