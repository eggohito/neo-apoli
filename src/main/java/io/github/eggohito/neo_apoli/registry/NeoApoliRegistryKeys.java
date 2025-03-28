package io.github.eggohito.neo_apoli.registry;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.type.EntityConditionType;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class NeoApoliRegistryKeys {

	public static final RegistryKey<Registry<Power>> POWER = create("power");
	public static final RegistryKey<Registry<PowerType<?>>> POWER_TYPE = create("power_type");

	public static final RegistryKey<Registry<EntityCondition>> ENTITY_CONDITION = create("entity_condition");
	public static final RegistryKey<Registry<EntityConditionType<?>>> ENTITY_CONDITION_TYPE = create("entity_condition_type");

	private static <T> RegistryKey<Registry<T>> create(String path) {
		return RegistryKey.ofRegistry(NeoApoli.id(path));
	}

}
