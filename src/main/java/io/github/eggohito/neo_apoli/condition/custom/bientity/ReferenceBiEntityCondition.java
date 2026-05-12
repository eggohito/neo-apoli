package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliBiEntityConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceBiEntityCondition(ResourceLocation value) implements BiEntityCondition, ReferenceMetaCondition<BiEntityCondition> {

	public static final MapCodec<ReferenceBiEntityCondition> MAP_CODEC = ReferenceMetaCondition.mapCodec(ReferenceBiEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceBiEntityCondition> STREAM_CODEC = ReferenceMetaCondition.streamCodec(ReferenceBiEntityCondition::new);

	@Override
	public BiEntityCondition.Kind targetKind() {
		return BiEntityCondition.Kind.INSTANCE;
	}

	@Override
	public BiEntityCondition.Type<?> getType() {
		return NeoApoliBiEntityConditionTypes.REFERENCE;
	}

}
