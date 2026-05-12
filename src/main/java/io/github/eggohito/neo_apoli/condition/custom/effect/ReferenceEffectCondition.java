package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliEffectConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceEffectCondition(ResourceLocation value) implements EffectCondition, ReferenceMetaCondition<EffectCondition> {

	public static final MapCodec<ReferenceEffectCondition> MAP_CODEC = ReferenceMetaCondition.mapCodec(ReferenceEffectCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceEffectCondition> STREAM_CODEC = ReferenceMetaCondition.streamCodec(ReferenceEffectCondition::new);

	@Override
	public EffectCondition.Type<?> getType() {
		return NeoApoliEffectConditionTypes.REFERENCE;
	}

	@Override
	public EffectCondition.Kind targetKind() {
		return EffectCondition.Kind.INSTANCE;
	}

}
