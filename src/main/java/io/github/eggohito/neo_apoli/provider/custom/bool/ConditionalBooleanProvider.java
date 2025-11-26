package io.github.eggohito.neo_apoli.provider.custom.bool;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.provider.custom.meta.ConditionalValueProvider;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderType;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record ConditionalBooleanProvider(Condition condition, BooleanProvider ifValue, BooleanProvider elseValue) implements BooleanProvider, ConditionalValueProvider<BooleanProvider, Boolean> {

	public static final MapCodec<ConditionalBooleanProvider> CODEC = MapCodecUtil.lazy(ConditionalBooleanProvider.class.getSimpleName(), () -> ConditionalValueProvider.createCodec(BooleanProvider.CODEC, ConditionalBooleanProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalBooleanProvider> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalBooleanProvider.class.getSimpleName(), () -> ConditionalValueProvider.createStreamCodec(BooleanProvider.STREAM_CODEC, ConditionalBooleanProvider::new));

	@Override
	public BooleanProviderType<?> getType() {
		return BooleanProviderTypes.CONDITIONAL;
	}

	@Override
	public @NotNull Boolean next(Context context) {
		return internalNextOrElse(context, () -> false);
	}

}
