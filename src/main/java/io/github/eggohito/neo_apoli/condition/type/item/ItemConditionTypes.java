package io.github.eggohito.neo_apoli.condition.type.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.item.*;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class ItemConditionTypes extends ConditionTypes {

	public static final ItemConditionType<AllOfItemCondition> ALL_OF = registerMetaInternal("all_of", AllOfItemCondition.CODEC, AllOfItemCondition.STREAM_CODEC);
	public static final ItemConditionType<AnyOfItemCondition> ANY_OF = registerMetaInternal("any_of", AnyOfItemCondition.CODEC, AnyOfItemCondition.STREAM_CODEC);
	public static final ItemConditionType<CompareItemCondition> COMPARE = registerMetaInternal("compare", CompareItemCondition.CODEC, CompareItemCondition.STREAM_CODEC);
	public static final ItemConditionType<CompareToRangeItemCondition> COMPARE_TO_RANGE = registerMetaInternal("compare_to_range", CompareToRangeItemCondition.CODEC, CompareToRangeItemCondition.STREAM_CODEC);
	public static final ItemConditionType<ConstantItemCondition> CONSTANT = registerMetaInternal("constant", ConstantItemCondition.CODEC, ConstantItemCondition.STREAM_CODEC);
	public static final ItemConditionType<DynamicItemCondition> DYNAMIC = registerMetaInternal("dynamic", DynamicItemCondition.CODEC, DynamicItemCondition.STREAM_CODEC);
	public static final ItemConditionType<InvertedItemCondition> INVERTED = registerMetaInternal("inverted", InvertedItemCondition.CODEC, InvertedItemCondition.STREAM_CODEC);
	public static final ItemConditionType<ReferenceItemCondition> REFERENCE = registerMetaInternal("reference", ReferenceItemCondition.CODEC, ReferenceItemCondition.STREAM_CODEC);

	public static final ItemConditionType<IsDamageableItemCondition> IS_DAMAGEABLE = registerInternal("is_damageable", IsDamageableItemCondition.CODEC, IsDamageableItemCondition.STREAM_CODEC);
	public static final ItemConditionType<IsEmptyItemCondition> IS_EMPTY = registerInternal("is_empty", IsEmptyItemCondition.CODEC, IsEmptyItemCondition.STREAM_CODEC);
	public static final ItemConditionType<IsEnchantableItemCondition> IS_ENCHANTABLE = registerInternal("is_enchantable", IsEnchantableItemCondition.CODEC, IsEnchantableItemCondition.STREAM_CODEC);
	public static final ItemConditionType<IsFoodItemCondition> IS_FOOD = registerInternal("is_food", IsFoodItemCondition.CODEC, IsFoodItemCondition.STREAM_CODEC);
	public static final ItemConditionType<MatchIngredientItemCondition> MATCH_INGREDIENT = registerInternal("match_ingredient", MatchIngredientItemCondition.CODEC, MatchIngredientItemCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends ItemCondition> ItemConditionType<C> registerMetaInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return registerMeta(NeoApoli.id(path), mapCodec, streamCodec);
	}

	private static <C extends ItemCondition> ItemConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends ItemCondition> ItemConditionType<C> registerMeta(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.ITEM_CONDITION_TYPE, id, new ItemConditionType<>(mapCodec, streamCodec));
	}

	public static <C extends ItemCondition> ItemConditionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		ResourceLocation prefixedId = id.withPrefix(ItemConditionType.PREFIX);
		return ConditionTypes.register(prefixedId, registerMeta(prefixedId, mapCodec, streamCodec));
	}

}
