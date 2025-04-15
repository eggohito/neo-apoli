package io.github.eggohito.neo_apoli.registry;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class NeoApoliRegistryKeys {

	public static final RegistryKey<Registry<Power.Type<?>>> POWER_TYPE = create("power_type");

	public static final RegistryKey<Registry<EntityConditionType<?>>> ENTITY_CONDITION_TYPE = create("entity_condition_type");
	public static final RegistryKey<Registry<EntityActionType<?>>> ENTITY_ACTION_TYPE = create("entity_action_type");

	public static final RegistryKey<Registry<NumberProvider.Type<?>>> NUMBER_PROVIDER_TYPE = create("number_provider_type");
	public static final RegistryKey<Registry<StringProvider.Type<?>>> STRING_PROVIDER_TYPE = create("string_provider_type");

	private static <T> RegistryKey<Registry<T>> create(String path) {
		return RegistryKey.ofRegistry(NeoApoli.id(path));
	}

}
