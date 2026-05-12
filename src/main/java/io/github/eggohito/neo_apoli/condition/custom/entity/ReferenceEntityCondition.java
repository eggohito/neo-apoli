package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliEntityConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceEntityCondition(ResourceLocation value) implements EntityCondition, ReferenceMetaCondition<EntityCondition> {

	public static final MapCodec<ReferenceEntityCondition> MAP_CODEC = ReferenceMetaCondition.mapCodec(ReferenceEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceEntityCondition> STREAM_CODEC = ReferenceMetaCondition.streamCodec(ReferenceEntityCondition::new);

	@Override
	public EntityCondition.Kind targetKind() {
		return EntityCondition.Kind.INSTANCE;
	}

	@Override
	public EntityCondition.Type<?> getType() {
		return NeoApoliEntityConditionTypes.REFERENCE;
	}

}
