package io.github.eggohito.neo_apoli.condition.type.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.item.ItemCondition;
import io.github.eggohito.neo_apoli.condition.custom.item.*;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ItemConditionTypes extends ConditionTypes {

	public static final ItemConditionType<AllOfItemCondition> ALL_OF = registerInternal("all_of", AllOfItemCondition.CODEC, AllOfItemCondition.PACKET_CODEC);
	public static final ItemConditionType<AnyOfItemCondition> ANY_OF = registerInternal("any_of", AnyOfItemCondition.CODEC, AnyOfItemCondition.PACKET_CODEC);
	public static final ItemConditionType<CompareItemCondition> COMPARE = registerInternal("compare", CompareItemCondition.CODEC, CompareItemCondition.PACKET_CODEC);
	public static final ItemConditionType<CompareToRangeItemCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeItemCondition.CODEC, CompareToRangeItemCondition.PACKET_CODEC);
	public static final ItemConditionType<ConstantItemCondition> CONSTANT = registerInternal("constant", ConstantItemCondition.CODEC, ConstantItemCondition.PACKET_CODEC);
	public static final ItemConditionType<InvertedItemCondition> INVERTED = registerInternal("inverted", InvertedItemCondition.CODEC, InvertedItemCondition.PACKET_CODEC);
	public static final ItemConditionType<ReferenceItemCondition> REFERENCE = registerInternal("reference", ReferenceItemCondition.CODEC, ReferenceItemCondition.PACKET_CODEC);

	public static final ItemConditionType<IsDamageableItemCondition> IS_DAMAGEABLE = registerInternal("is_damageable", IsDamageableItemCondition.CODEC, IsDamageableItemCondition.PACKET_CODEC);
	public static final ItemConditionType<IsEmptyItemCondition> IS_EMPTY = registerInternal("is_empty", IsEmptyItemCondition.CODEC, IsEmptyItemCondition.PACKET_CODEC);
	public static final ItemConditionType<IsEnchantableItemCondition> IS_ENCHANTABLE = registerInternal("is_enchantable", IsEnchantableItemCondition.CODEC, IsEnchantableItemCondition.PACKET_CODEC);
	public static final ItemConditionType<IsFoodItemCondition> IS_FOOD = registerInternal("is_food", IsFoodItemCondition.CODEC, IsFoodItemCondition.PACKET_CODEC);
	public static final ItemConditionType<MatchIngredientItemCondition> MATCH_INGREDIENT = registerInternal("match_ingredient", MatchIngredientItemCondition.CODEC, MatchIngredientItemCondition.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <C extends ItemCondition> ItemConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends ItemCondition> ItemConditionType<C> register(Identifier id, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(id.withPrefixedPath(ItemConditionType.PREFIX), Registry.register(NeoApoliRegistries.ITEM_CONDITION_TYPE, id, new ItemConditionType<>(mapCodec, packetCodec)));
	}

}
