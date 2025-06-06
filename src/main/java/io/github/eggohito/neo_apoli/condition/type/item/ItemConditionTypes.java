package io.github.eggohito.neo_apoli.condition.type.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.custom.item.IsDamageableItemCondition;
import io.github.eggohito.neo_apoli.condition.custom.item.IsEmptyItemCondition;
import io.github.eggohito.neo_apoli.condition.custom.item.IsEnchantableItemCondition;
import io.github.eggohito.neo_apoli.condition.custom.item.IsOfItemCondition;
import io.github.eggohito.neo_apoli.condition.meta.item.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ItemConditionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<ItemConditionType<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.ITEM_CONDITION_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, ItemConditionType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.ITEM_CONDITION_TYPE);

	public static final ItemConditionType<AllOfItemCondition> ALL_OF = registerInternal("all_of", AllOfItemCondition.CODEC, AllOfItemCondition.PACKET_CODEC);
	public static final ItemConditionType<AnyOfItemCondition> ANY_OF = registerInternal("any_of", AnyOfItemCondition.CODEC, AnyOfItemCondition.PACKET_CODEC);
	public static final ItemConditionType<CompareItemCondition> COMPARE = registerInternal("compare", CompareItemCondition.CODEC, CompareItemCondition.PACKET_CODEC);
	public static final ItemConditionType<ConstantItemCondition> CONSTANT = registerInternal("constant", ConstantItemCondition.CODEC, ConstantItemCondition.PACKET_CODEC);
	public static final ItemConditionType<InvertedItemCondition> INVERTED = registerInternal("inverted", InvertedItemCondition.CODEC, InvertedItemCondition.PACKET_CODEC);
	public static final ItemConditionType<ReferenceItemCondition> REFERENCE = registerInternal("reference", ReferenceItemCondition.CODEC, ReferenceItemCondition.PACKET_CODEC);

	public static final ItemConditionType<IsDamageableItemCondition> IS_DAMAGEABLE = registerInternal("is_damageable", IsDamageableItemCondition.CODEC, IsDamageableItemCondition.PACKET_CODEC);
	public static final ItemConditionType<IsEmptyItemCondition> IS_EMPTY = registerInternal("is_empty", IsEmptyItemCondition.CODEC, IsEmptyItemCondition.PACKET_CODEC);
	public static final ItemConditionType<IsEnchantableItemCondition> IS_ENCHANTABLE = registerInternal("is_enchantable", IsEnchantableItemCondition.CODEC, IsEnchantableItemCondition.PACKET_CODEC);
	public static final ItemConditionType<IsOfItemCondition> IS_OF = registerInternal("is_of", IsOfItemCondition.CODEC, IsOfItemCondition.PACKET_CODEC);

	public static void registerAll() {

		ALIASES.addPathAlias("and", RegistryUtil.getIdPath(NeoApoliRegistries.ITEM_CONDITION_TYPE, ALL_OF));
		ALIASES.addPathAlias("or", RegistryUtil.getIdPath(NeoApoliRegistries.ITEM_CONDITION_TYPE, ANY_OF));

		ALIASES.addPathAlias("damageable", RegistryUtil.getIdPath(NeoApoliRegistries.ITEM_CONDITION_TYPE, IS_DAMAGEABLE));
		ALIASES.addPathAlias("empty", RegistryUtil.getIdPath(NeoApoliRegistries.ITEM_CONDITION_TYPE, IS_EMPTY));
		ALIASES.addPathAlias("enchantable", RegistryUtil.getIdPath(NeoApoliRegistries.ITEM_CONDITION_TYPE, IS_ENCHANTABLE));

	}

	private static <C extends ItemCondition> ItemConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends ItemCondition> ItemConditionType<C> register(Identifier id, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return Registry.register(NeoApoliRegistries.ITEM_CONDITION_TYPE, id, new ItemConditionType<>(mapCodec, packetCodec));
	}

}
