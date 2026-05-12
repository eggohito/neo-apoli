package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliEntityConditionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public record IsOfEntityCondition(EntityType<?> entityType) implements EntityCondition {

	public static final MapCodec<IsOfEntityCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(EntityType.CODEC.fieldOf("entity_type").forGetter(IsOfEntityCondition::entityType))
		.apply(instance, IsOfEntityCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsOfEntityCondition> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.registry(Registries.ENTITY_TYPE), IsOfEntityCondition::entityType,
		IsOfEntityCondition::new
	);

	@Override
	public EntityCondition.Type<?> getType() {
		return NeoApoliEntityConditionTypes.IS_OF;
	}

	@Override
	public boolean test(Context context) {
		return context.getOptional(NeoApoliContextParams.THIS_ENTITY)
			.stream()
			.map(Entity::getType)
			.anyMatch(this.entityType()::equals);
	}

}
