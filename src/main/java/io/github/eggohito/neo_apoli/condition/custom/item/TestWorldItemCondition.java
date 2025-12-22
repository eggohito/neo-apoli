package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ITestWorldMetaCondition;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TestWorldItemCondition(WorldCondition condition) implements ItemCondition, ITestWorldMetaCondition {

	public static final MapCodec<TestWorldItemCondition> CODEC = ITestWorldMetaCondition.createCodec(TestWorldItemCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, TestWorldItemCondition> STREAM_CODEC = ITestWorldMetaCondition.createStreamCodec(TestWorldItemCondition::new);

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.TEST_WORLD;
	}

	@Override
	public String asDisplayString() {
		return ItemCondition.super.asDisplayString();
	}

}
