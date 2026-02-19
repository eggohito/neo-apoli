package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.ConditionalValueProvider;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderTypes;
import io.github.eggohito.neo_apoli.util.AABBUtil;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public record ConditionalBoxProvider(Condition condition, BoxProvider ifValue, BoxProvider elseValue) implements BoxProvider, ConditionalValueProvider<BoxProvider> {

	public static final MapCodec<ConditionalBoxProvider> MAP_CODEC = MapCodecUtil.lazy(ConditionalBoxProvider.class.getSimpleName(), () -> ConditionalValueProvider.mapCodec(BoxProvider.CODEC, ConditionalBoxProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalBoxProvider> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalBoxProvider.class.getSimpleName(), () -> ConditionalValueProvider.streamCodec(BoxProvider.STREAM_CODEC, ConditionalBoxProvider::new));

	@Override
	public @NotNull BoxProviderType<?> getType() {
		return BoxProviderTypes.CONDITIONAL;
	}

	@Override
	public @NotNull AABB nextBox(Context context) {
		return nextOrElse(context, BoxProvider::nextBox, () -> AABBUtil.EMPTY);
	}

}
