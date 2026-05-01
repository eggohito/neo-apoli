package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.kind.ConditionKind;
import io.github.eggohito.neo_apoli.condition.kind.custom.BiEntityConditionKind;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceBiEntityCondition(ResourceLocation value) implements BiEntityCondition, ReferenceMetaCondition<BiEntityCondition> {

	public static final MapCodec<ReferenceBiEntityCondition> MAP_CODEC = ReferenceMetaCondition.mapCodec(ReferenceBiEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceBiEntityCondition> STREAM_CODEC = ReferenceMetaCondition.streamCodec(ReferenceBiEntityCondition::new);

	@Override
	public ConditionKind<BiEntityCondition> targetKind() {
		return BiEntityConditionKind.INSTANCE;
	}

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.REFERENCE;
	}

}
