package io.github.eggohito.neo_apoli.provider.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.CompositeConditionalValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliEntityProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.conditional.CompositeConditional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record CompositeConditionalEntityProvider(List<CompositeConditional.Entry<EntityProvider>> entries, EntityProvider defaultValue) implements EntityProvider, CompositeConditionalValueProvider<EntityProvider> {

	public static final MapCodec<CompositeConditionalEntityProvider> CODEC = MapCodecUtil.lazy(CompositeConditionalEntityProvider.class.getSimpleName(), () -> CompositeConditionalValueProvider.mapCodec(EntityProvider.CODEC, CompositeConditionalEntityProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, CompositeConditionalEntityProvider> STREAM_CODEC = StreamCodecUtil.lazy(CompositeConditionalEntityProvider.class.getSimpleName(), () -> CompositeConditionalValueProvider.streamCodec(EntityProvider.STREAM_CODEC, CompositeConditionalEntityProvider::new));

	@Override
	public EntityProvider.@NotNull Type<?> getType() {
		return NeoApoliEntityProviderTypes.COMPOSITE_CONDITIONAL;
	}

	@Override
	public Optional<Entity> getEntity(Context context) {
		return this.getOrDefault(context, EntityProvider::getEntity);
	}

}
