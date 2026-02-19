package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.ChoiceValueProvider;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderTypes;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ChoiceBoxProvider(List<Case<Condition, BoxProvider>> cases, BoxProvider defaultValue) implements BoxProvider, ChoiceValueProvider<BoxProvider> {

	public static final MapCodec<ChoiceBoxProvider> MAP_CODEC = MapCodecUtil.lazy(ChoiceBoxProvider.class.getSimpleName(), () -> ChoiceValueProvider.mapCodec(BoxProvider.CODEC, ChoiceBoxProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChoiceBoxProvider> STREAM_CODEC = StreamCodecUtil.lazy(ChoiceBoxProvider.class.getSimpleName(), () -> ChoiceValueProvider.streamCodec(BoxProvider.STREAM_CODEC, ChoiceBoxProvider::new));

	@Override
	public @NotNull BoxProviderType<?> getType() {
		return BoxProviderTypes.CHOICE;
	}

	@Override
	public @NotNull AABB nextBox(Context context) {
		return nextOrDefault(context, BoxProvider::nextBox);
	}

}
