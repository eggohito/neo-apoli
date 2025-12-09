package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionType;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceEffectCondition(ResourceLocation value) implements EffectCondition, ReferenceMetaCondition<EffectCondition> {

	public static final MapCodec<ReferenceEffectCondition> CODEC = ReferenceMetaCondition.createCodec(ReferenceEffectCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceEffectCondition> STREAM_CODEC = ReferenceMetaCondition.createStreamCodec(ReferenceEffectCondition::new);

	@Override
	public EffectConditionType<?> getType() {
		return EffectConditionTypes.REFERENCE;
	}

	@Override
	public Pair<Class<EffectCondition>, String> classAndName() {
		return Pair.of(EffectCondition.class, "Mob effect condition");
	}

	@Override
	public String asDisplayString() {
		return EffectCondition.super.asDisplayString();
	}

}
