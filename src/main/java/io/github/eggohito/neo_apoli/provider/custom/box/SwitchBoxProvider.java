package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.SwitchValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliBoxProviderTypes;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SwitchBoxProvider(List<Case<Condition, BoxProvider>> cases, BoxProvider defaultValue) implements BoxProvider, SwitchValueProvider<BoxProvider> {

	public static final MapCodec<SwitchBoxProvider> MAP_CODEC = MapCodecUtil.lazy(SwitchBoxProvider.class.getSimpleName(), () -> SwitchValueProvider.mapCodec(BoxProvider.CODEC, SwitchBoxProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SwitchBoxProvider> STREAM_CODEC = StreamCodecUtil.lazy(SwitchBoxProvider.class.getSimpleName(), () -> SwitchValueProvider.streamCodec(BoxProvider.STREAM_CODEC, SwitchBoxProvider::new));

	@Override
	public @NotNull BoxProvider.Type<?> getType() {
		return NeoApoliBoxProviderTypes.SWITCH;
	}

	@Override
	public @NotNull AABB getBox(Context context) {
		return getOrDefault(context, BoxProvider::getBox);
	}

}
