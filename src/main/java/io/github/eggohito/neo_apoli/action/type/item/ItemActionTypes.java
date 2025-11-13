package io.github.eggohito.neo_apoli.action.type.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.custom.item.*;
import io.github.eggohito.neo_apoli.action.type.ActionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ItemActionTypes {

	public static final ItemActionType<ChoiceItemAction> CHOICE = registerInternal("choice", ChoiceItemAction.CODEC, ChoiceItemAction.PACKET_CODEC);
	public static final ItemActionType<ConditionalItemAction> CONDITIONAL = registerInternal("conditional", ConditionalItemAction.CODEC, ConditionalItemAction.PACKET_CODEC);
	public static final ItemActionType<LoopItemAction> LOOP = registerInternal("loop", LoopItemAction.CODEC, LoopItemAction.PACKET_CODEC);
	public static final ItemActionType<NothingItemAction> NOTHING = registerInternal("nothing", NothingItemAction.CODEC, NothingItemAction.PACKET_CODEC);
	public static final ItemActionType<RandomChanceItemAction> RANDOM_CHANCE = registerInternal("random_chance", RandomChanceItemAction.CODEC, RandomChanceItemAction.PACKET_CODEC);
	public static final ItemActionType<ReferenceItemAction> REFERENCE = registerInternal("reference", ReferenceItemAction.CODEC, ReferenceItemAction.PACKET_CODEC);
	public static final ItemActionType<SequenceItemAction> SEQUENCE = registerInternal("sequence", SequenceItemAction.CODEC, SequenceItemAction.PACKET_CODEC);
	public static final ItemActionType<WeightedItemAction> WEIGHTED = registerInternal("weighted", WeightedItemAction.CODEC, WeightedItemAction.PACKET_CODEC);

	public static final ItemActionType<ConsumeItemAction> CONSUME = registerInternal("consume", ConsumeItemAction.CODEC, ConsumeItemAction.PACKET_CODEC);
	public static final ItemActionType<DamageItemAction> DAMAGE = registerInternal("damage", DamageItemAction.CODEC, DamageItemAction.PACKET_CODEC);
	public static final ItemActionType<ModifyItemAction> MODIFY = registerInternal("modify", ModifyItemAction.CODEC, ModifyItemAction.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <C extends ItemAction> ItemActionType<C> registerInternal(String path, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends ItemAction> ItemActionType<C> register(Identifier id, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return ActionTypes.register(id.withPrefixedPath(ItemActionType.PREFIX), Registry.register(NeoApoliRegistries.ITEM_ACTION_TYPE, id, new ItemActionType<>(mapCodec, packetCodec)));
	}

}
