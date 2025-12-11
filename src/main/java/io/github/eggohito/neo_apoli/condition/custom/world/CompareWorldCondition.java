package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionType;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CompareWorldCondition(Comparison comparison) implements WorldCondition, CompareMetaCondition {

	public static final MapCodec<CompareWorldCondition> CODEC = CompareMetaCondition.createCodec(CompareWorldCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareWorldCondition> STREAM_CODEC = CompareMetaCondition.createStreamCodec(CompareWorldCondition::new);

	@Override
	public WorldConditionType<?> getType() {
		return WorldConditionTypes.COMPARE;
	}

	@Override
	public String asDisplayString() {
		return WorldCondition.super.asDisplayString();
	}

}
