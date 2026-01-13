package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public record IsInTagEntityCondition(TagKey<EntityType<?>> tag) implements EntityCondition {

	public static final MapCodec<IsInTagEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(TagKey.hashedCodec(Registries.ENTITY_TYPE).fieldOf("tag").forGetter(IsInTagEntityCondition::tag))
		.apply(instance, IsInTagEntityCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsInTagEntityCondition> STREAM_CODEC = StreamCodec.composite(
		TagKey.streamCodec(Registries.ENTITY_TYPE), IsInTagEntityCondition::tag,
		IsInTagEntityCondition::new
	);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_IN_TAG;
	}

	@Override
	public boolean test(Context context) {

		try {
			return context.optional(NeoApoliContextKeys.THIS_ENTITY)
				.stream()
				.map(Entity::getType)
				.filter(type -> context.markActive(this))
				.anyMatch(type -> type.is(this.tag()));
		}

		finally {
			context.markInActive(this);
		}

	}

}
