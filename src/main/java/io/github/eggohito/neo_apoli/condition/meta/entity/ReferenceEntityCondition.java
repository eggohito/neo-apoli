package io.github.eggohito.neo_apoli.condition.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import io.github.eggohito.neo_apoli.condition.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record ReferenceEntityCondition(Identifier value) implements EntityCondition, ReferenceMetaCondition<EntityCondition, EntityConditionType<?>> {

	public static final MapCodec<ReferenceEntityCondition> CODEC = ReferenceMetaCondition.codec(ReferenceEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceEntityCondition> PACKET_CODEC = ReferenceMetaCondition.packetCodec(ReferenceEntityCondition::new);

	@Override
	public ConditionCategory<EntityCondition> getCategory() {
		return EntityCondition.super.getCategory();
	}

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.REFERENCE;
	}

}
