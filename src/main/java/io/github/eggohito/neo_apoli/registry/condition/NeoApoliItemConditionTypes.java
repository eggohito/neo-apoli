package io.github.eggohito.neo_apoli.registry.condition;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.item.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class NeoApoliItemConditionTypes {

	public static final ItemCondition.Type<AllOfItemCondition> ALL_OF = registerInternal("all_of", AllOfItemCondition.MAP_CODEC, AllOfItemCondition.STREAM_CODEC);
	public static final ItemCondition.Type<AnyOfItemCondition> ANY_OF = registerInternal("any_of", AnyOfItemCondition.MAP_CODEC, AnyOfItemCondition.STREAM_CODEC);
	public static final ItemCondition.Type<CompareItemCondition> COMPARE = registerInternal("compare", CompareItemCondition.MAP_CODEC, CompareItemCondition.STREAM_CODEC);
	public static final ItemCondition.Type<CompareToRangeItemCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeItemCondition.MAP_CODEC, CompareToRangeItemCondition.STREAM_CODEC);
	public static final ItemCondition.Type<ConstantItemCondition> CONSTANT = registerInternal("constant", ConstantItemCondition.MAP_CODEC, ConstantItemCondition.STREAM_CODEC);
	public static final ItemCondition.Type<DynamicItemCondition> DYNAMIC = registerInternal("dynamic", DynamicItemCondition.MAP_CODEC, DynamicItemCondition.STREAM_CODEC);
	public static final ItemCondition.Type<InvertedItemCondition> INVERTED = registerInternal("inverted", InvertedItemCondition.MAP_CODEC, InvertedItemCondition.STREAM_CODEC);
	public static final ItemCondition.Type<ReferenceItemCondition> REFERENCE = registerInternal("reference", ReferenceItemCondition.MAP_CODEC, ReferenceItemCondition.STREAM_CODEC);
	public static final ItemCondition.Type<TestWorldItemCondition> TEST_WORLD = registerInternal("test_world", TestWorldItemCondition.MAP_CODEC, TestWorldItemCondition.STREAM_CODEC);

	public static final ItemCondition.Type<IsDamageableItemCondition> IS_DAMAGEABLE = registerInternal("is_damageable", IsDamageableItemCondition.MAP_CODEC, IsDamageableItemCondition.STREAM_CODEC);
	public static final ItemCondition.Type<IsEmptyItemCondition> IS_EMPTY = registerInternal("is_empty", IsEmptyItemCondition.MAP_CODEC, IsEmptyItemCondition.STREAM_CODEC);
	public static final ItemCondition.Type<IsEnchantableItemCondition> IS_ENCHANTABLE = registerInternal("is_enchantable", IsEnchantableItemCondition.MAP_CODEC, IsEnchantableItemCondition.STREAM_CODEC);
	public static final ItemCondition.Type<IsFoodItemCondition> IS_FOOD = registerInternal("is_food", IsFoodItemCondition.MAP_CODEC, IsFoodItemCondition.STREAM_CODEC);
	public static final ItemCondition.Type<MatchIngredientItemCondition> MATCH_INGREDIENT = registerInternal("match_ingredient", MatchIngredientItemCondition.MAP_CODEC, MatchIngredientItemCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends ItemCondition> ItemCondition.Type<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends ItemCondition> ItemCondition.Type<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.ITEM_CONDITION_TYPE, id, new ItemCondition.Type<>(mapCodec, streamCodec));
	}

}
