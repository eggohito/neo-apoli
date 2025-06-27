package io.github.eggohito.neo_apoli.action.type.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.custom.item.ApplyModifierItemAction;
import io.github.eggohito.neo_apoli.action.custom.item.ConsumeItemAction;
import io.github.eggohito.neo_apoli.action.custom.item.DamageItemAction;
import io.github.eggohito.neo_apoli.action.meta.item.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ItemActionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<ItemActionType<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.ITEM_ACTION_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, ItemActionType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.ITEM_ACTION_TYPE);

	public static final ItemActionType<ExecuteCommandItemAction> EXECUTE_COMMAND = registerInternal("execute_command", ExecuteCommandItemAction.CODEC, ExecuteCommandItemAction.PACKET_CODEC);
	public static final ItemActionType<IfElseItemAction> IF_ELSE = registerInternal("if_else", IfElseItemAction.CODEC, IfElseItemAction.PACKET_CODEC);
	public static final ItemActionType<IfElseListItemAction> IF_ELSE_LIST = registerInternal("if_else_list", IfElseListItemAction.CODEC, IfElseListItemAction.PACKET_CODEC);
	public static final ItemActionType<NothingItemAction> NOTHING = registerInternal("nothing", NothingItemAction.CODEC, NothingItemAction.PACKET_CODEC);
	public static final ItemActionType<RandomChanceItemAction> RANDOM_CHANCE = registerInternal("random_chance", RandomChanceItemAction.CODEC, RandomChanceItemAction.PACKET_CODEC);
	public static final ItemActionType<RandomChoiceItemAction> RANDOM_CHOICE = registerInternal("random_choice", RandomChoiceItemAction.CODEC, RandomChoiceItemAction.PACKET_CODEC);
	public static final ItemActionType<ReferenceItemAction> REFERENCE = registerInternal("reference", ReferenceItemAction.CODEC, ReferenceItemAction.PACKET_CODEC);
	public static final ItemActionType<SequenceItemAction> SEQUENCE = registerInternal("sequence", SequenceItemAction.CODEC, SequenceItemAction.PACKET_CODEC);

	public static final ItemActionType<ApplyModifierItemAction> APPLY_MODIFIER = registerInternal("apply_modifier", ApplyModifierItemAction.CODEC, ApplyModifierItemAction.PACKET_CODEC);
	public static final ItemActionType<ConsumeItemAction> CONSUME = registerInternal("consume", ConsumeItemAction.CODEC, ConsumeItemAction.PACKET_CODEC);
	public static final ItemActionType<DamageItemAction> DAMAGE = registerInternal("damage", DamageItemAction.CODEC, DamageItemAction.PACKET_CODEC);

	public static void registerAll() {

		ALIASES.addPathAlias("no_op", RegistryUtil.getIdPath(NeoApoliRegistries.ITEM_ACTION_TYPE, NOTHING));
		ALIASES.addPathAlias("chance", RegistryUtil.getIdPath(NeoApoliRegistries.ITEM_ACTION_TYPE, RANDOM_CHANCE));
		ALIASES.addPathAlias("choice", RegistryUtil.getIdPath(NeoApoliRegistries.ITEM_ACTION_TYPE, RANDOM_CHOICE));
		ALIASES.addPathAlias("and", RegistryUtil.getIdPath(NeoApoliRegistries.ITEM_ACTION_TYPE, SEQUENCE));

	}

	private static <A extends ItemAction> ItemActionType<A> registerInternal(String path, MapCodec<A> mapCodec, PacketCodec<RegistryByteBuf, A> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <A extends ItemAction> ItemActionType<A> register(Identifier id, MapCodec<A> mapCodec, PacketCodec<RegistryByteBuf, A> packetCodec) {
		return Registry.register(NeoApoliRegistries.ITEM_ACTION_TYPE, id, new ItemActionType<>(mapCodec, packetCodec));
	}

}
