package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceCondition(ResourceLocation value) implements ReferenceMetaCondition<Condition> {

	public static final MapCodec<ReferenceCondition> MAP_CODEC = ReferenceMetaCondition.mapCodec(ReferenceCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceCondition> STREAM_CODEC = ReferenceMetaCondition.streamCodec(ReferenceCondition::new);

	@Override
	public Pair<Class<Condition>, String> classAndName() {
		return Pair.of(Condition.class, "Condition");
	}

	@Override
	public ConditionType<?> getType() {
		return ConditionTypes.REFERENCE;
	}

}
