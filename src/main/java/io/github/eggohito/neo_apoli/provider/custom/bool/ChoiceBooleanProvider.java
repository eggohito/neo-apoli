package io.github.eggohito.neo_apoli.provider.custom.bool;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.ChoiceValueProvider;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderType;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderTypes;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ChoiceBooleanProvider(List<Case<Condition, BooleanProvider>> cases, BooleanProvider defaultValue) implements BooleanProvider, ChoiceValueProvider<BooleanProvider> {

	public static final MapCodec<ChoiceBooleanProvider> MAP_CODEC = MapCodecUtil.lazy(ChoiceBooleanProvider.class.getSimpleName(), () -> ChoiceValueProvider.mapCodec(BooleanProvider.CODEC, ChoiceBooleanProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChoiceBooleanProvider> STREAM_CODEC = StreamCodecUtil.lazy(ChoiceBooleanProvider.class.getSimpleName(), () -> ChoiceValueProvider.streamCodec(BooleanProvider.STREAM_CODEC, ChoiceBooleanProvider::new));

	@Override
	public @NotNull BooleanProviderType<?> getType() {
		return BooleanProviderTypes.CHOICE;
	}

	@Override
	public boolean nextBoolean(Context context) {
		return nextOrDefault(context, BooleanProvider::nextBoolean);
	}

}
