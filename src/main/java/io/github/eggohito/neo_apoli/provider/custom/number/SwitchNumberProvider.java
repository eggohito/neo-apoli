package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.SwitchValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SwitchNumberProvider(List<Case<Condition, NumberProvider>> cases, NumberProvider defaultValue) implements NumberProvider, SwitchValueProvider<NumberProvider> {

	public static final MapCodec<SwitchNumberProvider> CODEC = MapCodecUtil.lazy(SwitchNumberProvider.class.getSimpleName(), () -> SwitchValueProvider.mapCodec(NumberProvider.CODEC, SwitchNumberProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SwitchNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(SwitchNumberProvider.class.getSimpleName(), () -> SwitchValueProvider.streamCodec(NumberProvider.STREAM_CODEC, SwitchNumberProvider::new));

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.SWITCH;
	}

	@Override
	public double getDouble(Context context) {
		return this.nextOrDefault(context, NumberProvider::getDouble);
	}

}
