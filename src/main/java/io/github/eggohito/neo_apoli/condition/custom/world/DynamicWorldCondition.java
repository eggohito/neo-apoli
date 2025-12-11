package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.DynamicMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionType;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicWorldCondition(BooleanProvider value) implements WorldCondition, DynamicMetaCondition {

	public static final MapCodec<DynamicWorldCondition> CODEC = DynamicMetaCondition.createCodec(DynamicWorldCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicWorldCondition> STREAM_CODEC = DynamicMetaCondition.createStreamCodec(DynamicWorldCondition::new);

	@Override
	public WorldConditionType<?> getType() {
		return WorldConditionTypes.DYNAMIC;
	}

	@Override
	public String asDisplayString() {
		return WorldCondition.super.asDisplayString();
	}

}
