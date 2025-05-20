package io.github.eggohito.neo_apoli.condition.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import io.github.eggohito.neo_apoli.condition.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record ReferenceBiEntityCondition(Identifier value) implements BiEntityCondition, ReferenceMetaCondition<BiEntityCondition, BiEntityConditionType<?>> {

	public static final MapCodec<ReferenceBiEntityCondition> CODEC = ReferenceMetaCondition.codec(ReferenceBiEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceBiEntityCondition> PACKET_CODEC = ReferenceMetaCondition.packetCodec(ReferenceBiEntityCondition::new);

	@Override
	public ConditionCategory<BiEntityCondition> getCategory() {
		return BiEntityCondition.super.getCategory();
	}

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.REFERENCE;
	}

}
