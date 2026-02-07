package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceBiEntityCondition(ResourceLocation value) implements BiEntityCondition, IReferenceMetaCondition<BiEntityCondition> {

	public static final MapCodec<ReferenceBiEntityCondition> MAP_CODEC = IReferenceMetaCondition.mapCodec(ReferenceBiEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceBiEntityCondition> STREAM_CODEC = IReferenceMetaCondition.streamCodec(ReferenceBiEntityCondition::new);

	@Override
	public Pair<Class<BiEntityCondition>, String> classAndName() {
		return Pair.of(BiEntityCondition.class, "Bi-entity condition");
	}

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.REFERENCE;
	}

}
