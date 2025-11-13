package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public record IsInTagEntityCondition(TagKey<EntityType<?>> tag) implements EntityCondition {

	public static final MapCodec<IsInTagEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(TagKey.codec(RegistryKeys.ENTITY_TYPE).fieldOf("tag").forGetter(IsInTagEntityCondition::tag))
		.apply(instance, IsInTagEntityCondition::new));

	public static final PacketCodec<RegistryByteBuf, IsInTagEntityCondition> PACKET_CODEC = PacketCodec.tuple(
		TagKey.packetCodec(RegistryKeys.ENTITY_TYPE), IsInTagEntityCondition::tag,
		IsInTagEntityCondition::new
	);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_IN_TAG;
	}

	@Override
	public boolean test(Context context) {

		try {
			return context.optional(ContextParameters.THIS_ENTITY)
				.stream()
				.map(Entity::getType)
				.filter(type -> context.markActive(this))
				.anyMatch(type -> type.isIn(this.tag()));
		}

		finally {
			context.markInActive(this);
		}

	}

}
