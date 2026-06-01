package io.github.eggohito.neo_apoli.provider.custom.slot;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.SwitchValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliSlotProviderTypes;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.SlotAccess;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SwitchSlotProvider(List<Case<Condition, SlotProvider>> cases, SlotProvider defaultValue) implements SlotProvider, SwitchValueProvider<SlotProvider> {

	public static final MapCodec<SwitchSlotProvider> CODEC = MapCodecUtil.lazy(SwitchSlotProvider.class.getSimpleName(), () -> SwitchValueProvider.mapCodec(SlotProvider.CODEC, SwitchSlotProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SwitchSlotProvider> STREAM_CODEC = StreamCodecUtil.lazy(SwitchSlotProvider.class.getSimpleName(), () -> SwitchValueProvider.streamCodec(SlotProvider.STREAM_CODEC, SwitchSlotProvider::new));

	@Override
	public SlotProvider.@NotNull Type<?> getType() {
		return NeoApoliSlotProviderTypes.SWITCH;
	}

	@Override
	public @NotNull SlotAccess getSlot(Context context) {
		return this.getOrDefault(context, SlotProvider::getSlot);
	}

}
