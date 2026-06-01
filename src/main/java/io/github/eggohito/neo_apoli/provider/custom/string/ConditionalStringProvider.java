package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.ConditionalValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliStringProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record ConditionalStringProvider(Condition condition, StringProvider ifValue, StringProvider elseValue) implements StringProvider, ConditionalValueProvider<StringProvider> {

	public static final MapCodec<ConditionalStringProvider> MAP_CODEC = MapCodecUtil.lazy(ConditionalStringProvider.class.getSimpleName(), () -> ConditionalValueProvider.mapCodec(StringProvider.CODEC, ConditionalStringProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalStringProvider> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalStringProvider.class.getSimpleName(), () -> ConditionalValueProvider.streamCodec(StringProvider.STREAM_CODEC, ConditionalStringProvider::new));

	@Override
	public @NotNull StringProvider.Type<?> getType() {
		return NeoApoliStringProviderTypes.CONDITIONAL;
	}

	@Override
	public @NotNull String getString(Context context) {
		return getOrElse(context, StringProvider::getString, () -> "");
	}

}
