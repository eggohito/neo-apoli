package io.github.eggohito.neo_apoli.registry;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.util.comparison.type.ComparisonType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class NeoApoliRegistryKeys {

	public static final RegistryKey<Registry<PowerType<?>>> POWER_TYPE = create("power_type");

	public static final RegistryKey<Registry<ActionCategory<?>>> ACTION_CATEGORY = create("action_category");
	public static final RegistryKey<Registry<ConditionCategory<?>>> CONDITION_CATEGORY = create("condition_category");

	public static final RegistryKey<Registry<EntityConditionType<?>>> ENTITY_CONDITION_TYPE = create("entity_condition_type");
	public static final RegistryKey<Registry<EntityActionType<?>>> ENTITY_ACTION_TYPE = create("entity_action_type");

	public static final RegistryKey<Registry<BlockConditionType<?>>> BLOCK_CONDITION_TYPE = create("block_condition_type");
	public static final RegistryKey<Registry<BlockActionType<?>>> BLOCK_ACTION_TYPE = create("block_action_type");

	public static final RegistryKey<Registry<NumberProviderType<?>>> NUMBER_PROVIDER_TYPE = create("number_provider_type");
	public static final RegistryKey<Registry<StringProviderType<?>>> STRING_PROVIDER_TYPE = create("string_provider_type");

	public static final RegistryKey<Registry<ComparisonType<?>>> COMPARISON_TYPE = create("comparison_type");

	private static <T> RegistryKey<Registry<T>> create(String path) {
		return RegistryKey.ofRegistry(NeoApoli.id(path));
	}

}
