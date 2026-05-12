package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.DynamicMetaCondition;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliWorldConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicWorldCondition(BooleanProvider value) implements WorldCondition, DynamicMetaCondition {

	public static final MapCodec<DynamicWorldCondition> MAP_CODEC = DynamicMetaCondition.mapCodec(DynamicWorldCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicWorldCondition> STREAM_CODEC = DynamicMetaCondition.streamCodec(DynamicWorldCondition::new);

	@Override
	public WorldCondition.Type<?> getType() {
		return NeoApoliWorldConditionTypes.DYNAMIC;
	}

}
