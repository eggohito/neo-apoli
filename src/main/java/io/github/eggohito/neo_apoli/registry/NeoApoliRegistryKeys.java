package io.github.eggohito.neo_apoli.registry;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.util.comparison.type.ComparisonType;
import io.github.eggohito.neo_apoli.util.container_type.ContainerType;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class NeoApoliRegistryKeys {

	public static final RegistryKey<Registry<PowerType<?>>> POWER_TYPE = create("power_type");
	public static final RegistryKey<Registry<Power>> POWER = create("power");

	public static final RegistryKey<Registry<ActionCategory<?>>> ACTION_CATEGORY = create("action_category");
	public static final RegistryKey<Registry<ConditionCategory<?>>> CONDITION_CATEGORY = create("condition_category");

	public static final RegistryKey<Registry<BiEntityActionType<?>>> BIENTITY_ACTION_TYPE = create("bientity_action_type");
	public static final RegistryKey<Registry<BiEntityAction>> BIENTITY_ACTION = create("bientity_action");

	public static final RegistryKey<Registry<BiEntityConditionType<?>>> BIENTITY_CONDITION_TYPE = create("bientity_condition_type");
	public static final RegistryKey<Registry<BiEntityCondition>> BIENTITY_CONDITION = create("bientity_condition");

	public static final RegistryKey<Registry<BlockConditionType<?>>> BLOCK_CONDITION_TYPE = create("block_condition_type");
	public static final RegistryKey<Registry<BlockCondition>> BLOCK_CONDITION = create("block_condition");

	public static final RegistryKey<Registry<BlockActionType<?>>> BLOCK_ACTION_TYPE = create("block_action_type");
	public static final RegistryKey<Registry<BlockAction>> BLOCK_ACTION = create("block_action");

	public static final RegistryKey<Registry<EntityConditionType<?>>> ENTITY_CONDITION_TYPE = create("entity_condition_type");
	public static final RegistryKey<Registry<EntityCondition>> ENTITY_CONDITION = create("entity_condition");

	public static final RegistryKey<Registry<EntityActionType<?>>> ENTITY_ACTION_TYPE = create("entity_action_type");
	public static final RegistryKey<Registry<EntityAction>> ENTITY_ACTION = create("entity_action");

	public static final RegistryKey<Registry<ItemConditionType<?>>> ITEM_CONDITION_TYPE = create("item_condition_type");
	public static final RegistryKey<Registry<ItemCondition>> ITEM_CONDITION = create("item_condition");

	public static final RegistryKey<Registry<ItemActionType<?>>> ITEM_ACTION_TYPE = create("item_action_type");
	public static final RegistryKey<Registry<ItemAction>> ITEM_ACTION = create("item_action");

	public static final RegistryKey<Registry<NumberProviderType<?>>> NUMBER_PROVIDER_TYPE = create("number_provider_type");
	public static final RegistryKey<Registry<StringProviderType<?>>> STRING_PROVIDER_TYPE = create("string_provider_type");

	public static final RegistryKey<Registry<ComparisonType<?>>> COMPARISON_TYPE = create("comparison_type");
	public static final RegistryKey<Registry<ContainerType>> CONTAINER_TYPE = create("container_type");
	public static final RegistryKey<Registry<ModifierType<?>>> MODIFIER_TYPE = create("modifier_type");

	private static <T> RegistryKey<Registry<T>> create(String path) {
		return RegistryKey.ofRegistry(NeoApoli.id(path));
	}

}
