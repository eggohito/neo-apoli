package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;

public record IsOfEntityCondition(EntityType<?> entityType) implements EntityCondition {

	public static final MapCodec<IsOfEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(EntityType.CODEC.fieldOf("entity_type").forGetter(IsOfEntityCondition::entityType))
		.apply(instance, IsOfEntityCondition::new));

	public static final PacketCodec<RegistryByteBuf, IsOfEntityCondition> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.registryValue(RegistryKeys.ENTITY_TYPE), IsOfEntityCondition::entityType,
		IsOfEntityCondition::new
	);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_OF;
	}

	@Override
	public boolean test(Context context) {
		return context.optional(NeoApoliContextParameters.THIS_ENTITY)
			.stream()
			.map(Entity::getType)
			.anyMatch(this.entityType()::equals);
	}

}
