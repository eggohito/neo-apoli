package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionType;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceWorldCondition(ResourceLocation value) implements WorldCondition, IReferenceMetaCondition<WorldCondition> {

	public static final MapCodec<ReferenceWorldCondition> CODEC = IReferenceMetaCondition.createCodec(ReferenceWorldCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceWorldCondition> STREAM_CODEC = IReferenceMetaCondition.createStreamCodec(ReferenceWorldCondition::new);

	@Override
	public Pair<Class<WorldCondition>, String> classAndName() {
		return Pair.of(WorldCondition.class, "World condition");
	}

	@Override
	public WorldConditionType<?> getType() {
		return WorldConditionTypes.REFERENCE;
	}

	@Override
	public String asDisplayString() {
		return WorldCondition.super.asDisplayString();
	}

}
