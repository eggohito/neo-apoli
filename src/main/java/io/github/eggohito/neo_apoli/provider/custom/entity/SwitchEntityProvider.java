package io.github.eggohito.neo_apoli.provider.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.SwitchValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliEntityProviderTypes;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record SwitchEntityProvider(List<Case<Condition, EntityProvider>> cases, EntityProvider defaultValue) implements EntityProvider, SwitchValueProvider<EntityProvider> {

	public static final MapCodec<SwitchEntityProvider> CODEC = MapCodecUtil.lazy(SwitchEntityProvider.class.getSimpleName(), () -> SwitchValueProvider.mapCodec(EntityProvider.CODEC, SwitchEntityProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SwitchEntityProvider> STREAM_CODEC = StreamCodecUtil.lazy(SwitchEntityProvider.class.getSimpleName(), () -> SwitchValueProvider.streamCodec(EntityProvider.STREAM_CODEC, SwitchEntityProvider::new));

	@Override
	public EntityProvider.@NotNull Type<?> getType() {
		return NeoApoliEntityProviderTypes.SWITCH;
	}

	@Override
	public Optional<Entity> getEntity(Context context) {
		return this.getOrDefault(context, EntityProvider::getEntity);
	}

}
