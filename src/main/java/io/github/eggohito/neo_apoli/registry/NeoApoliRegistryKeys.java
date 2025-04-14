package io.github.eggohito.neo_apoli.registry;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class NeoApoliRegistryKeys {

	public static final RegistryKey<Registry<Power>> POWER = create("power");
	public static final RegistryKey<Registry<PowerType<?>>> POWER_TYPE = create("power_type");

	public static final RegistryKey<Registry<ValueProvider>> VALUE_PROVIDER = create("value_provider");
	public static final RegistryKey<Registry<ValueProviderType<?>>> VALUE_PROVIDER_TYPE = create("value_provider_type");

	public static final RegistryKey<Registry<EntityCondition>> ENTITY_CONDITION = create("entity_condition");
	public static final RegistryKey<Registry<EntityConditionType<?>>> ENTITY_CONDITION_TYPE = create("entity_condition_type");

	public static final RegistryKey<Registry<EntityAction>> ENTITY_ACTION = create("entity_action");
	public static final RegistryKey<Registry<EntityActionType<?>>> ENTITY_ACTION_TYPE = create("entity_action_type");

	private static <T> RegistryKey<Registry<T>> create(String path) {
		return RegistryKey.ofRegistry(NeoApoli.id(path));
	}

}
