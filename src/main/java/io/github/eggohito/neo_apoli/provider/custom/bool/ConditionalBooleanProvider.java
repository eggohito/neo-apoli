package io.github.eggohito.neo_apoli.provider.custom.bool;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.ConditionalValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliBooleanProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record ConditionalBooleanProvider(Condition condition, BooleanProvider ifValue, BooleanProvider elseValue) implements BooleanProvider, ConditionalValueProvider<BooleanProvider> {

	public static final MapCodec<ConditionalBooleanProvider> MAP_CODEC = MapCodecUtil.lazy(ConditionalBooleanProvider.class.getSimpleName(), () -> ConditionalValueProvider.mapCodec(BooleanProvider.CODEC, ConditionalBooleanProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalBooleanProvider> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalBooleanProvider.class.getSimpleName(), () -> ConditionalValueProvider.streamCodec(BooleanProvider.STREAM_CODEC, ConditionalBooleanProvider::new));

	@Override
	public @NotNull BooleanProvider.Type<?> getType() {
		return NeoApoliBooleanProviderTypes.CONDITIONAL;
	}

	@Override
	public boolean getBoolean(Context context) {
		return nextOrElse(context, BooleanProvider::getBoolean, () -> false);
	}

}
