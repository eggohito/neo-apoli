package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.DynamicMetaCondition;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliEffectConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicEffectCondition(BooleanProvider value) implements EffectCondition, DynamicMetaCondition {

	public static final MapCodec<DynamicEffectCondition> MAP_CODEC = DynamicMetaCondition.mapCodec(DynamicEffectCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicEffectCondition> STREAM_CODEC = DynamicMetaCondition.streamCodec(DynamicEffectCondition::new);

	@Override
	public EffectCondition.Type<?> getType() {
		return NeoApoliEffectConditionTypes.DYNAMIC;
	}

}
