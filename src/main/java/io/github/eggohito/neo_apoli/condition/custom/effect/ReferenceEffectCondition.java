package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionType;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceEffectCondition(ResourceLocation value) implements EffectCondition, IReferenceMetaCondition<EffectCondition> {

	public static final MapCodec<ReferenceEffectCondition> CODEC = IReferenceMetaCondition.createCodec(ReferenceEffectCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceEffectCondition> STREAM_CODEC = IReferenceMetaCondition.createStreamCodec(ReferenceEffectCondition::new);

	@Override
	public EffectConditionType<?> getType() {
		return EffectConditionTypes.REFERENCE;
	}

	@Override
	public Pair<Class<EffectCondition>, String> classAndName() {
		return Pair.of(EffectCondition.class, "Effect condition");
	}

	@Override
	public String asDisplayString() {
		return EffectCondition.super.asDisplayString();
	}

}
