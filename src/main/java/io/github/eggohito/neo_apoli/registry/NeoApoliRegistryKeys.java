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
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.power.PowerEntry;
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
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class NeoApoliRegistryKeys {

	public static final ResourceKey<Registry<PowerEntry<?>>> POWER = create("power");
	public static final ResourceKey<Registry<PowerType<?>>> POWER_TYPE = create("power_type");

	public static final ResourceKey<Registry<Action>> ACTION = create("action");
	public static final ResourceKey<Registry<BiEntityAction>> BIENTITY_ACTION = create("action/bientity");
	public static final ResourceKey<Registry<BlockAction>> BLOCK_ACTION = create("action/block");
	public static final ResourceKey<Registry<EntityAction>> ENTITY_ACTION = create("action/entity");
	public static final ResourceKey<Registry<ItemAction>> ITEM_ACTION = create("action/item");

	public static final ResourceKey<Registry<Condition>> CONDITION = create("condition");
	public static final ResourceKey<Registry<BiEntityCondition>> BIENTITY_CONDITION = create("condition/bientity");
	public static final ResourceKey<Registry<BlockCondition>> BLOCK_CONDITION = create("condition/block");
	public static final ResourceKey<Registry<DamageCondition>> DAMAGE_CONDITION = create("condition/damage");
	public static final ResourceKey<Registry<EntityCondition>> ENTITY_CONDITION = create("condition/entity");
	public static final ResourceKey<Registry<ItemCondition>> ITEM_CONDITION = create("condition/item");

	public static final ResourceKey<Registry<ActionType<?>>> ACTION_TYPE = create("action_type");
	public static final ResourceKey<Registry<BiEntityActionType<?>>> BIENTITY_ACTION_TYPE = create("action_type/bientity");
	public static final ResourceKey<Registry<BlockActionType<?>>> BLOCK_ACTION_TYPE = create("action_type/block");
	public static final ResourceKey<Registry<EntityActionType<?>>> ENTITY_ACTION_TYPE = create("action_type/entity");
	public static final ResourceKey<Registry<ItemActionType<?>>> ITEM_ACTION_TYPE = create("action_type/item");

	public static final ResourceKey<Registry<ConditionType<?>>> CONDITION_TYPE = create("condition_type");
	public static final ResourceKey<Registry<BiEntityConditionType<?>>> BIENTITY_CONDITION_TYPE = create("condition_type/bientity");
	public static final ResourceKey<Registry<BlockConditionType<?>>> BLOCK_CONDITION_TYPE = create("condition_type/block");
	public static final ResourceKey<Registry<DamageConditionType<?>>> DAMAGE_CONDITION_TYPE = create("condition_type/damage");
	public static final ResourceKey<Registry<EntityConditionType<?>>> ENTITY_CONDITION_TYPE = create("condition_type/entity");
	public static final ResourceKey<Registry<ItemConditionType<?>>> ITEM_CONDITION_TYPE = create("condition_type/item");
	public static final ResourceKey<Registry<KeyConditionType<?>>> KEY_CONDITION_TYPE = create("condition_type/key");

	public static final ResourceKey<Registry<BooleanProviderType<?>>> BOOLEAN_PROVIDER_TYPE = create("provider_type/bool");
	public static final ResourceKey<Registry<BoxProviderType<?>>> BOX_PROVIDER_TYPE = create("provider_type/box");
	public static final ResourceKey<Registry<NbtProviderType<?>>> NBT_PROVIDER_TYPE = create("provider_type/nbt");
	public static final ResourceKey<Registry<NumberProviderType<?>>> NUMBER_PROVIDER_TYPE = create("provider_type/number");
	public static final ResourceKey<Registry<StringProviderType<?>>> STRING_PROVIDER_TYPE = create("provider_type/string");
	public static final ResourceKey<Registry<Vec3dProviderType<?>>> VEC3D_PROVIDER_TYPE = create("provider_type/vec3d");

	public static final ResourceKey<Registry<ComparisonType<?>>> COMPARISON_TYPE = create("comparison_type");
	public static final ResourceKey<Registry<ContainerType>> CONTAINER_TYPE = create("container_type");
	public static final ResourceKey<Registry<ModifierType<?>>> MODIFIER_TYPE = create("modifier_type");
	public static final ResourceKey<Registry<ColorType<?>>> COLOR_TYPE = create("color_type");

	public static final ResourceKey<Registry<TypedContextKey<?>>> TYPED_CONTEXT_KEY = create("typed_context_key");

	private static <T> ResourceKey<Registry<T>> create(String path) {
		return ResourceKey.createRegistryKey(NeoApoli.id(path));
	}

}
