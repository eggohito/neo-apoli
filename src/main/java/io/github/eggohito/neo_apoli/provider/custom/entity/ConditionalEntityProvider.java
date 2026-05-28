package io.github.eggohito.neo_apoli.provider.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.ConditionalValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliEntityProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

public record ConditionalEntityProvider(Condition condition, EntityProvider ifValue, EntityProvider elseValue) implements EntityProvider, ConditionalValueProvider<EntityProvider> {

	public static final MapCodec<ConditionalEntityProvider> CODEC = MapCodecUtil.lazy(ConditionalEntityProvider.class.getSimpleName(), () -> ConditionalValueProvider.mapCodec(EntityProvider.CODEC, ConditionalEntityProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalEntityProvider> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalEntityProvider.class.getSimpleName(), () -> ConditionalValueProvider.streamCodec(EntityProvider.STREAM_CODEC, ConditionalEntityProvider::new));

	@Override
	public EntityProvider.Type<?> getType() {
		return NeoApoliEntityProviderTypes.CONDITIONAL;
	}

	@Override
	public Optional<Entity> getEntity(Context context) {
		return this.nextOrElse(context, EntityProvider::getEntity, Optional::empty);
	}

}
