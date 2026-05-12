package io.github.eggohito.neo_apoli.registry;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.bientity.BiEntityAction;
import io.github.eggohito.neo_apoli.action.custom.block.BlockAction;
import io.github.eggohito.neo_apoli.action.custom.entity.EntityAction;
import io.github.eggohito.neo_apoli.action.custom.item.ItemAction;
import io.github.eggohito.neo_apoli.color.Color;
import io.github.eggohito.neo_apoli.comparison.Comparison;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.bientity.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.condition.custom.damage.DamageCondition;
import io.github.eggohito.neo_apoli.condition.custom.effect.EffectCondition;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.fluid.FluidCondition;
import io.github.eggohito.neo_apoli.condition.custom.item.ItemCondition;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.container_menu.ContainerMenu;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerHolder;
import io.github.eggohito.neo_apoli.power.global.GlobalPowerSet;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.box.BoxProvider;
import io.github.eggohito.neo_apoli.provider.custom.nbt.NbtProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class NeoApoliRegistryKeys {

	public static final ResourceKey<Registry<PowerHolder<?>>> POWER = create("power");
	public static final ResourceKey<Registry<GlobalPowerSet>> GLOBAL_POWER_SET = create("global_power_set");

	public static final ResourceKey<Registry<Power.Type<?>>> POWER_TYPE = create("power_type");

	public static final ResourceKey<Registry<Action.Kind<?>>> ACTION_KIND = create("action_kind");
	public static final ResourceKey<Registry<Condition.Kind<?>>> CONDITION_KIND = create("condition_kind");

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
	public static final ResourceKey<Registry<EffectCondition>> EFFECT_CONDITION = create("condition/effect");
	public static final ResourceKey<Registry<FluidCondition>> FLUID_CONDITION = create("condition/fluid");
	public static final ResourceKey<Registry<WorldCondition>> WORLD_CONDITION = create("condition/world");

	public static final ResourceKey<Registry<Action.Type<?>>> ACTION_TYPE = create("action_type");
	public static final ResourceKey<Registry<BiEntityAction.Type<?>>> BIENTITY_ACTION_TYPE = create("action_type/bientity");
	public static final ResourceKey<Registry<BlockAction.Type<?>>> BLOCK_ACTION_TYPE = create("action_type/block");
	public static final ResourceKey<Registry<EntityAction.Type<?>>> ENTITY_ACTION_TYPE = create("action_type/entity");
	public static final ResourceKey<Registry<ItemAction.Type<?>>> ITEM_ACTION_TYPE = create("action_type/item");

	public static final ResourceKey<Registry<Condition.Type<?>>> CONDITION_TYPE = create("condition_type");
	public static final ResourceKey<Registry<BiEntityCondition.Type<?>>> BIENTITY_CONDITION_TYPE = create("condition_type/bientity");
	public static final ResourceKey<Registry<BlockCondition.Type<?>>> BLOCK_CONDITION_TYPE = create("condition_type/block");
	public static final ResourceKey<Registry<DamageCondition.Type<?>>> DAMAGE_CONDITION_TYPE = create("condition_type/damage");
	public static final ResourceKey<Registry<EntityCondition.Type<?>>> ENTITY_CONDITION_TYPE = create("condition_type/entity");
	public static final ResourceKey<Registry<ItemCondition.Type<?>>> ITEM_CONDITION_TYPE = create("condition_type/item");
	public static final ResourceKey<Registry<EffectCondition.Type<?>>> EFFECT_CONDITION_TYPE = create("condition_type/effect");
	public static final ResourceKey<Registry<FluidCondition.Type<?>>> FLUID_CONDITION_TYPE = create("condition_type/fluid");
	public static final ResourceKey<Registry<WorldCondition.Type<?>>> WORLD_CONDITION_TYPE = create("condition_type/world");

	public static final ResourceKey<Registry<BooleanProvider.Type<?>>> BOOLEAN_PROVIDER_TYPE = create("provider_type/bool");
	public static final ResourceKey<Registry<BoxProvider.Type<?>>> BOX_PROVIDER_TYPE = create("provider_type/box");
	public static final ResourceKey<Registry<NbtProvider.Type<?>>> NBT_PROVIDER_TYPE = create("provider_type/nbt");
	public static final ResourceKey<Registry<NumberProvider.Type<?>>> NUMBER_PROVIDER_TYPE = create("provider_type/number");
	public static final ResourceKey<Registry<StringProvider.Type<?>>> STRING_PROVIDER_TYPE = create("provider_type/string");
	public static final ResourceKey<Registry<Vec3Provider.Type<?>>> VEC3_PROVIDER_TYPE = create("provider_type/vec3");

	public static final ResourceKey<Registry<Comparison.Type<?>>> COMPARISON_TYPE = create("comparison_type");
	public static final ResourceKey<Registry<ContainerMenu.Type<?>>> CONTAINER_MENU_TYPE = create("container_menu_type");
	public static final ResourceKey<Registry<Modifier.Type<?>>> MODIFIER_TYPE = create("modifier_type");
	public static final ResourceKey<Registry<Color.Type<?>>> COLOR_TYPE = create("color_type");
	public static final ResourceKey<Registry<HudElement.Type<?>>> HUD_ELEMENT_TYPE = create("hud_element_type");

	public static final ResourceKey<Registry<Context.Parameter<?>>> CONTEXT_PARAMETER = create("context/parameter");

	private static <T> ResourceKey<Registry<T>> create(String path) {
		return ResourceKey.createRegistryKey(NeoApoli.id(path));
	}

}
