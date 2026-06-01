package io.github.eggohito.neo_apoli.provider.custom.bool;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.SwitchValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliBooleanProviderTypes;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SwitchBooleanProvider(List<Case<Condition, BooleanProvider>> cases, BooleanProvider defaultValue) implements BooleanProvider, SwitchValueProvider<BooleanProvider> {

	public static final MapCodec<SwitchBooleanProvider> MAP_CODEC = MapCodecUtil.lazy(SwitchBooleanProvider.class.getSimpleName(), () -> SwitchValueProvider.mapCodec(BooleanProvider.CODEC, SwitchBooleanProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SwitchBooleanProvider> STREAM_CODEC = StreamCodecUtil.lazy(SwitchBooleanProvider.class.getSimpleName(), () -> SwitchValueProvider.streamCodec(BooleanProvider.STREAM_CODEC, SwitchBooleanProvider::new));

	@Override
	public @NotNull BooleanProvider.Type<?> getType() {
		return NeoApoliBooleanProviderTypes.SWITCH;
	}

	@Override
	public boolean getBoolean(Context context) {
		return getOrDefault(context, BooleanProvider::getBoolean);
	}

}
