package io.github.eggohito.neo_apoli.registry;

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
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.box.BoxProvider;
import io.github.eggohito.neo_apoli.provider.custom.nbt.NbtProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class NeoApoliRegistries {

	public static final Registry<Power.Type<?>> POWER_TYPE = create(NeoApoliRegistryKeys.POWER_TYPE);

	public static final Registry<Action.Kind<?>> ACTION_KIND = create(NeoApoliRegistryKeys.ACTION_KIND);
	public static final Registry<Condition.Kind<?>> CONDITION_KIND = create(NeoApoliRegistryKeys.CONDITION_KIND);

	public static final Registry<Action.Type<?>> ACTION_TYPE = create(NeoApoliRegistryKeys.ACTION_TYPE);
	public static final Registry<BiEntityAction.Type<?>> BIENTITY_ACTION_TYPE = create(NeoApoliRegistryKeys.BIENTITY_ACTION_TYPE);
	public static final Registry<BlockAction.Type<?>> BLOCK_ACTION_TYPE = create(NeoApoliRegistryKeys.BLOCK_ACTION_TYPE);
	public static final Registry<EntityAction.Type<?>> ENTITY_ACTION_TYPE = create(NeoApoliRegistryKeys.ENTITY_ACTION_TYPE);
	public static final Registry<ItemAction.Type<?>> ITEM_ACTION_TYPE = create(NeoApoliRegistryKeys.ITEM_ACTION_TYPE);

	public static final Registry<Condition.Type<?>> CONDITION_TYPE = create(NeoApoliRegistryKeys.CONDITION_TYPE);
	public static final Registry<BiEntityCondition.Type<?>> BIENTITY_CONDITION_TYPE = create(NeoApoliRegistryKeys.BIENTITY_CONDITION_TYPE);
	public static final Registry<BlockCondition.Type<?>> BLOCK_CONDITION_TYPE = create(NeoApoliRegistryKeys.BLOCK_CONDITION_TYPE);
	public static final Registry<DamageCondition.Type<?>> DAMAGE_CONDITION_TYPE = create(NeoApoliRegistryKeys.DAMAGE_CONDITION_TYPE);
	public static final Registry<EntityCondition.Type<?>> ENTITY_CONDITION_TYPE = create(NeoApoliRegistryKeys.ENTITY_CONDITION_TYPE);
	public static final Registry<ItemCondition.Type<?>> ITEM_CONDITION_TYPE = create(NeoApoliRegistryKeys.ITEM_CONDITION_TYPE);
	public static final Registry<EffectCondition.Type<?>> EFFECT_CONDITION_TYPE = create(NeoApoliRegistryKeys.EFFECT_CONDITION_TYPE);
	public static final Registry<FluidCondition.Type<?>> FLUID_CONDITION_TYPE = create(NeoApoliRegistryKeys.FLUID_CONDITION_TYPE);
	public static final Registry<WorldCondition.Type<?>> WORLD_CONDITION_TYPE = create(NeoApoliRegistryKeys.WORLD_CONDITION_TYPE);

	public static final Registry<BooleanProvider.Type<?>> BOOLEAN_PROVIDER_TYPE = create(NeoApoliRegistryKeys.BOOLEAN_PROVIDER_TYPE);
	public static final Registry<BoxProvider.Type<?>> BOX_PROVIDER_TYPE = create(NeoApoliRegistryKeys.BOX_PROVIDER_TYPE);
	public static final Registry<NbtProvider.Type<?>> NBT_PROVIDER_TYPE = create(NeoApoliRegistryKeys.NBT_PROVIDER_TYPE);
	public static final Registry<NumberProvider.Type<?>> NUMBER_PROVIDER_TYPE = create(NeoApoliRegistryKeys.NUMBER_PROVIDER_TYPE);
	public static final Registry<StringProvider.Type<?>> STRING_PROVIDER_TYPE = create(NeoApoliRegistryKeys.STRING_PROVIDER_TYPE);
	public static final Registry<Vec3Provider.Type<?>> VEC3_PROVIDER_TYPE = create(NeoApoliRegistryKeys.VEC3_PROVIDER_TYPE);

	public static final Registry<Comparison.Type<?>> COMPARISON_TYPE = create(NeoApoliRegistryKeys.COMPARISON_TYPE);
	public static final Registry<ContainerMenu.Type<?>> CONTAINER_MENU_TYPE = create(NeoApoliRegistryKeys.CONTAINER_MENU_TYPE);
	public static final Registry<Modifier.Type<?>> MODIFIER_TYPE = create(NeoApoliRegistryKeys.MODIFIER_TYPE);
	public static final Registry<Color.Type<?>> COLOR_TYPE = create(NeoApoliRegistryKeys.COLOR_TYPE);
	public static final Registry<HudElement.Type<?>> HUD_ELEMENT_TYPE = create(NeoApoliRegistryKeys.HUD_ELEMENT_TYPE);

	public static final Registry<Context.Parameter<?>> CONTEXT_PARAMETER = create(NeoApoliRegistryKeys.CONTEXT_PARAMETER);

	private static <T> Registry<T> create(ResourceKey<Registry<T>> key) {
		return FabricRegistryBuilder.createSimple(key).buildAndRegister();
	}

}
