package io.github.eggohito.neo_apoli.registry;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.bientity.BiEntityAction;
import io.github.eggohito.neo_apoli.action.custom.block.BlockAction;
import io.github.eggohito.neo_apoli.action.custom.entity.EntityAction;
import io.github.eggohito.neo_apoli.action.custom.item.ItemAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.bientity.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.condition.custom.damage.DamageCondition;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.item.ItemCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderType;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderType;
import io.github.eggohito.neo_apoli.util.color.type.ColorType;
import io.github.eggohito.neo_apoli.util.comparison.type.ComparisonType;
import io.github.eggohito.neo_apoli.util.container_type.ContainerType;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class NeoApoliRegistryKeys {

	public static final RegistryKey<Registry<Power>> POWER = create("power");
	public static final RegistryKey<Registry<PowerType<?>>> POWER_TYPE = create("power_type");

	public static final RegistryKey<Registry<Action>> ACTION = create("action");
	public static final RegistryKey<Registry<BiEntityAction>> BIENTITY_ACTION = create("action/bientity");
	public static final RegistryKey<Registry<BlockAction>> BLOCK_ACTION = create("action/block");
	public static final RegistryKey<Registry<EntityAction>> ENTITY_ACTION = create("action/entity");
	public static final RegistryKey<Registry<ItemAction>> ITEM_ACTION = create("action/item");

	public static final RegistryKey<Registry<Condition>> CONDITION = create("condition");
	public static final RegistryKey<Registry<BiEntityCondition>> BIENTITY_CONDITION = create("condition/bientity");
	public static final RegistryKey<Registry<BlockCondition>> BLOCK_CONDITION = create("condition/block");
	public static final RegistryKey<Registry<DamageCondition>> DAMAGE_CONDITION = create("condition/damage");
	public static final RegistryKey<Registry<EntityCondition>> ENTITY_CONDITION = create("condition/entity");
	public static final RegistryKey<Registry<ItemCondition>> ITEM_CONDITION = create("condition/item");

	public static final RegistryKey<Registry<ActionType<?>>> ACTION_TYPE = create("action_type");
	public static final RegistryKey<Registry<BiEntityActionType<?>>> BIENTITY_ACTION_TYPE = create("action_type/bientity");
	public static final RegistryKey<Registry<BlockActionType<?>>> BLOCK_ACTION_TYPE = create("action_type/block");
	public static final RegistryKey<Registry<EntityActionType<?>>> ENTITY_ACTION_TYPE = create("action_type/entity");
	public static final RegistryKey<Registry<ItemActionType<?>>> ITEM_ACTION_TYPE = create("action_type/item");

	public static final RegistryKey<Registry<ConditionType<?>>> CONDITION_TYPE = create("condition_type");
	public static final RegistryKey<Registry<BiEntityConditionType<?>>> BIENTITY_CONDITION_TYPE = create("condition_type/bientity");
	public static final RegistryKey<Registry<BlockConditionType<?>>> BLOCK_CONDITION_TYPE = create("condition_type/block");
	public static final RegistryKey<Registry<DamageConditionType<?>>> DAMAGE_CONDITION_TYPE = create("condition_type/damage");
	public static final RegistryKey<Registry<EntityConditionType<?>>> ENTITY_CONDITION_TYPE = create("condition_type/entity");
	public static final RegistryKey<Registry<ItemConditionType<?>>> ITEM_CONDITION_TYPE = create("condition_type/item");

	public static final RegistryKey<Registry<BooleanProviderType<?>>> BOOLEAN_PROVIDER_TYPE = create("provider_type/bool");
	public static final RegistryKey<Registry<BoxProviderType<?>>> BOX_PROVIDER_TYPE = create("provider_type/box");
	public static final RegistryKey<Registry<NbtProviderType<?>>> NBT_PROVIDER_TYPE = create("provider_type/nbt");
	public static final RegistryKey<Registry<NumberProviderType<?>>> NUMBER_PROVIDER_TYPE = create("provider_type/number");
	public static final RegistryKey<Registry<StringProviderType<?>>> STRING_PROVIDER_TYPE = create("provider_type/string");
	public static final RegistryKey<Registry<Vec3dProviderType<?>>> VEC3D_PROVIDER_TYPE = create("provider_type/vec3d");

	public static final RegistryKey<Registry<ComparisonType<?>>> COMPARISON_TYPE = create("comparison_type");
	public static final RegistryKey<Registry<ContainerType>> CONTAINER_TYPE = create("container_type");
	public static final RegistryKey<Registry<ModifierType<?>>> MODIFIER_TYPE = create("modifier_type");
	public static final RegistryKey<Registry<ColorType<?>>> COLOR_TYPE = create("color_type");

	private static <T> RegistryKey<Registry<T>> create(String path) {
		return RegistryKey.ofRegistry(NeoApoli.id(path));
	}

}
