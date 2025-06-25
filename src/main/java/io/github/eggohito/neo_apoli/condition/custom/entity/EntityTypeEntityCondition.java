package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.EntityType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;

import java.util.Objects;

@EqualsAndHashCode(callSuper = false)
@Data
public final class EntityTypeEntityCondition extends EntityCondition {

	public static final MapCodec<EntityTypeEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityType.CODEC.fieldOf("entity_type").forGetter(EntityTypeEntityCondition::entityType)
	).apply(instance, EntityTypeEntityCondition::new));

	public static final PacketCodec<RegistryByteBuf, EntityTypeEntityCondition> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.registryValue(RegistryKeys.ENTITY_TYPE), EntityTypeEntityCondition::entityType,
		EntityTypeEntityCondition::new
	);

	private final EntityType<?> entityType;

	public EntityTypeEntityCondition(EntityType<?> entityType) {
		this.entityType = entityType;
	}

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.ENTITY_TYPE;
	}

	@Override
	protected boolean impl(Context context) {
		return Objects.equals(context.required(ContextParameters.THIS_ENTITY).getType(), this.entityType());
	}

}
