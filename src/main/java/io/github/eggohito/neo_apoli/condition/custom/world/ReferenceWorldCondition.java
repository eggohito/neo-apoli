package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionType;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceWorldCondition(ResourceLocation value) implements WorldCondition, ReferenceMetaCondition<WorldCondition> {

	public static final MapCodec<ReferenceWorldCondition> MAP_CODEC = ReferenceMetaCondition.mapCodec(ReferenceWorldCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceWorldCondition> STREAM_CODEC = ReferenceMetaCondition.streamCodec(ReferenceWorldCondition::new);

	@Override
	public Pair<Class<WorldCondition>, String> classAndName() {
		return Pair.of(WorldCondition.class, "World condition");
	}

	@Override
	public WorldConditionType<?> getType() {
		return WorldConditionTypes.REFERENCE;
	}

}
