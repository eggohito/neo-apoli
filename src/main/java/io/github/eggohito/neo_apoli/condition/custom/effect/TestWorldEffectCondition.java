package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ITestWorldMetaCondition;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionType;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TestWorldEffectCondition(WorldCondition condition) implements EffectCondition, ITestWorldMetaCondition {

	public static final MapCodec<TestWorldEffectCondition> MAP_CODEC = ITestWorldMetaCondition.mapCodec(TestWorldEffectCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, TestWorldEffectCondition> STREAM_CODEC = ITestWorldMetaCondition.streamCodec(TestWorldEffectCondition::new);

	@Override
	public EffectConditionType<?> getType() {
		return EffectConditionTypes.TEST_WORLD;
	}

}
