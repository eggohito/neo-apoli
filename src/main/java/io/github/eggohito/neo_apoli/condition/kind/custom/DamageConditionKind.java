package io.github.eggohito.neo_apoli.condition.kind.custom;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.condition.custom.damage.DamageCondition;
import io.github.eggohito.neo_apoli.condition.kind.ConditionKind;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public enum DamageConditionKind implements ConditionKind<DamageCondition> {

	INSTANCE;

	@Override
	public @Nullable Function<String, CommandBuilder> commandBuilder() {
		return null;
	}

	@Override
	public ResourceKey<? extends Registry<DamageCondition>> registryKey() {
		return NeoApoliRegistryKeys.DAMAGE_CONDITION;
	}

	@Override
	public Codec<DamageCondition> codec() {
		return DamageCondition.CODEC;
	}

	@Override
	public String asDisplayString() {
		return "Damage condition";
	}

}
