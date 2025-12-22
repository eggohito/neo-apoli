package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceEntityCondition(ResourceLocation value) implements EntityCondition, IReferenceMetaCondition<EntityCondition> {

	public static final MapCodec<ReferenceEntityCondition> CODEC = IReferenceMetaCondition.createCodec(ReferenceEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceEntityCondition> STREAM_CODEC = IReferenceMetaCondition.createStreamCodec(ReferenceEntityCondition::new);

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
