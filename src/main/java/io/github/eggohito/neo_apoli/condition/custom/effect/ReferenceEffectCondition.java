package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.kind.ConditionKind;
import io.github.eggohito.neo_apoli.condition.kind.custom.EffectConditionKind;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionType;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceEffectCondition(ResourceLocation value) implements EffectCondition, ReferenceMetaCondition<EffectCondition> {

	public static final MapCodec<ReferenceEffectCondition> MAP_CODEC = ReferenceMetaCondition.mapCodec(ReferenceEffectCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceEffectCondition> STREAM_CODEC = ReferenceMetaCondition.streamCodec(ReferenceEffectCondition::new);

	@Override
	public EffectConditionType<?> getType() {
		return EffectConditionTypes.REFERENCE;
	}

	@Override
	public ConditionKind<EffectCondition> targetKind() {
		return EffectConditionKind.INSTANCE;
	}

}
