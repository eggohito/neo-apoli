package io.github.eggohito.neo_apoli.condition.kind.custom;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.condition.custom.effect.EffectCondition;
import io.github.eggohito.neo_apoli.condition.kind.ConditionKind;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public enum EffectConditionKind implements ConditionKind<EffectCondition> {

	INSTANCE;

	@Override
	public @Nullable Function<String, CommandBuilder> commandBuilder() {
		return null;
	}

	@Override
	public ResourceKey<? extends Registry<EffectCondition>> registryKey() {
		return NeoApoliRegistryKeys.EFFECT_CONDITION;
	}

	@Override
	public Codec<EffectCondition> codec() {
		return EffectCondition.CODEC;
	}

	@Override
	public String asDisplayString() {
		return "Effect condition";
	}

}
