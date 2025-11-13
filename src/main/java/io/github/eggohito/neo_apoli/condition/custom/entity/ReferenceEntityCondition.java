package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record ReferenceEntityCondition(Identifier value) implements EntityCondition, ReferenceMetaCondition<EntityCondition> {

	public static final MapCodec<ReferenceEntityCondition> CODEC = ReferenceMetaCondition.codec(ReferenceEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceEntityCondition> PACKET_CODEC = ReferenceMetaCondition.packetCodec(ReferenceEntityCondition::new);

	@Override
	public Pair<Class<EntityCondition>, String> classAndName() {
		return Pair.of(EntityCondition.class, "Entity condition");
	}

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.REFERENCE;
	}

	@Override
	public String asDisplayString() {
		return EntityCondition.super.asDisplayString();
	}

}
