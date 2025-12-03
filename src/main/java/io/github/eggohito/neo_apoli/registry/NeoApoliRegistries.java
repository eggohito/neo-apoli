package io.github.eggohito.neo_apoli.registry;

import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.hud.type.HudElementType;
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
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class NeoApoliRegistries {

	public static final Registry<PowerType<?>> POWER_TYPE = create(NeoApoliRegistryKeys.POWER_TYPE);

	public static final Registry<ActionType<?>> ACTION_TYPE = create(NeoApoliRegistryKeys.ACTION_TYPE);
	public static final Registry<BiEntityActionType<?>> BIENTITY_ACTION_TYPE = create(NeoApoliRegistryKeys.BIENTITY_ACTION_TYPE);
	public static final Registry<BlockActionType<?>> BLOCK_ACTION_TYPE = create(NeoApoliRegistryKeys.BLOCK_ACTION_TYPE);
	public static final Registry<EntityActionType<?>> ENTITY_ACTION_TYPE = create(NeoApoliRegistryKeys.ENTITY_ACTION_TYPE);
	public static final Registry<ItemActionType<?>> ITEM_ACTION_TYPE = create(NeoApoliRegistryKeys.ITEM_ACTION_TYPE);

	public static final Registry<ConditionType<?>> CONDITION_TYPE = create(NeoApoliRegistryKeys.CONDITION_TYPE);
	public static final Registry<BiEntityConditionType<?>> BIENTITY_CONDITION_TYPE = create(NeoApoliRegistryKeys.BIENTITY_CONDITION_TYPE);
	public static final Registry<BlockConditionType<?>> BLOCK_CONDITION_TYPE = create(NeoApoliRegistryKeys.BLOCK_CONDITION_TYPE);
	public static final Registry<DamageConditionType<?>> DAMAGE_CONDITION_TYPE = create(NeoApoliRegistryKeys.DAMAGE_CONDITION_TYPE);
	public static final Registry<EntityConditionType<?>> ENTITY_CONDITION_TYPE = create(NeoApoliRegistryKeys.ENTITY_CONDITION_TYPE);
	public static final Registry<ItemConditionType<?>> ITEM_CONDITION_TYPE = create(NeoApoliRegistryKeys.ITEM_CONDITION_TYPE);
	public static final Registry<KeyConditionType<?>> KEY_CONDITION_TYPE = create(NeoApoliRegistryKeys.KEY_CONDITION_TYPE);

	public static final Registry<BooleanProviderType<?>> BOOLEAN_PROVIDER_TYPE = create(NeoApoliRegistryKeys.BOOLEAN_PROVIDER_TYPE);
	public static final Registry<BoxProviderType<?>> BOX_PROVIDER_TYPE = create(NeoApoliRegistryKeys.BOX_PROVIDER_TYPE);
	public static final Registry<NbtProviderType<?>> NBT_PROVIDER_TYPE = create(NeoApoliRegistryKeys.NBT_PROVIDER_TYPE);
	public static final Registry<NumberProviderType<?>> NUMBER_PROVIDER_TYPE = create(NeoApoliRegistryKeys.NUMBER_PROVIDER_TYPE);
	public static final Registry<StringProviderType<?>> STRING_PROVIDER_TYPE = create(NeoApoliRegistryKeys.STRING_PROVIDER_TYPE);
	public static final Registry<Vec3dProviderType<?>> VEC3D_PROVIDER_TYPE = create(NeoApoliRegistryKeys.VEC3D_PROVIDER_TYPE);

	public static final Registry<ComparisonType<?>> COMPARISON_TYPE = create(NeoApoliRegistryKeys.COMPARISON_TYPE);
	public static final Registry<ContainerType> CONTAINER_TYPE = create(NeoApoliRegistryKeys.CONTAINER_TYPE);
	public static final Registry<ModifierType<?>> MODIFIER_TYPE = create(NeoApoliRegistryKeys.MODIFIER_TYPE);
	public static final Registry<ColorType<?>> COLOR_TYPE = create(NeoApoliRegistryKeys.COLOR_TYPE);
	public static final Registry<HudElementType<?>> HUD_ELEMENT_TYPE = create(NeoApoliRegistryKeys.HUD_ELEMENT_TYPE);

	public static final Registry<TypedContextKey<?>> TYPED_CONTEXT_KEY = create(NeoApoliRegistryKeys.TYPED_CONTEXT_KEY);

	private static <T> Registry<T> create(ResourceKey<Registry<T>> key) {
		return FabricRegistryBuilder.createSimple(key).buildAndRegister();
	}

}
