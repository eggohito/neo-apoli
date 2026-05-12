package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.TestWorldMetaCondition;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliEffectConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TestWorldEffectCondition(WorldCondition condition) implements EffectCondition, TestWorldMetaCondition {

	public static final MapCodec<TestWorldEffectCondition> MAP_CODEC = TestWorldMetaCondition.mapCodec(TestWorldEffectCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, TestWorldEffectCondition> STREAM_CODEC = TestWorldMetaCondition.streamCodec(TestWorldEffectCondition::new);

	@Override
	public EffectCondition.Type<?> getType() {
		return NeoApoliEffectConditionTypes.TEST_WORLD;
	}

}
