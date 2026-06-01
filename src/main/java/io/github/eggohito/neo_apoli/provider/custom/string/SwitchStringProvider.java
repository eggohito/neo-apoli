package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.SwitchValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliStringProviderTypes;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SwitchStringProvider(List<Case<Condition, StringProvider>> cases, StringProvider defaultValue) implements StringProvider, SwitchValueProvider<StringProvider> {

	public static final MapCodec<SwitchStringProvider> MAP_CODEC = MapCodecUtil.lazy(SwitchStringProvider.class.getSimpleName(), () -> SwitchValueProvider.mapCodec(StringProvider.CODEC, SwitchStringProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SwitchStringProvider> STREAM_CODEC = StreamCodecUtil.lazy(SwitchStringProvider.class.getSimpleName(), () -> SwitchValueProvider.streamCodec(StringProvider.STREAM_CODEC, SwitchStringProvider::new));

	@Override
	public @NotNull StringProvider.Type<?> getType() {
		return NeoApoliStringProviderTypes.SWITCH;
	}

	@Override
	public @NotNull String getString(Context context) {
		return getOrDefault(context, StringProvider::getString);
	}

}
