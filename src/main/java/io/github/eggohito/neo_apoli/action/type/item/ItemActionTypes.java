package io.github.eggohito.neo_apoli.action.type.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.custom.item.*;
import io.github.eggohito.neo_apoli.action.type.ActionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class ItemActionTypes {

	public static final ItemActionType<ConditionalItemAction> CONDITIONAL = registerInternal("conditional", ConditionalItemAction.MAP_CODEC, ConditionalItemAction.STREAM_CODEC);
	public static final ItemActionType<LoopItemAction> LOOP = registerInternal("loop", LoopItemAction.MAP_CODEC, LoopItemAction.STREAM_CODEC);
	public static final ItemActionType<NothingItemAction> NOTHING = registerInternal("nothing", NothingItemAction.MAP_CODEC, NothingItemAction.STREAM_CODEC);
	public static final ItemActionType<RandomChanceItemAction> RANDOM_CHANCE = registerInternal("random_chance", RandomChanceItemAction.MAP_CODEC, RandomChanceItemAction.STREAM_CODEC);
	public static final ItemActionType<ReferenceItemAction> REFERENCE = registerInternal("reference", ReferenceItemAction.MAP_CODEC, ReferenceItemAction.STREAM_CODEC);
	public static final ItemActionType<SequenceItemAction> SEQUENCE = registerInternal("sequence", SequenceItemAction.MAP_CODEC, SequenceItemAction.STREAM_CODEC);
	public static final ItemActionType<SwitchItemAction> SWITCH = registerInternal("switch", SwitchItemAction.MAP_CODEC, SwitchItemAction.STREAM_CODEC);
	public static final ItemActionType<WeightedItemAction> WEIGHTED = registerInternal("weighted", WeightedItemAction.MAP_CODEC, WeightedItemAction.STREAM_CODEC);

	public static final ItemActionType<ConsumeItemAction> CONSUME = registerInternal("consume", ConsumeItemAction.MAP_CODEC, ConsumeItemAction.STREAM_CODEC);
	public static final ItemActionType<DamageItemAction> DAMAGE = registerInternal("damage", DamageItemAction.MAP_CODEC, DamageItemAction.STREAM_CODEC);
	public static final ItemActionType<ModifyItemAction> MODIFY = registerInternal("modify", ModifyItemAction.MAP_CODEC, ModifyItemAction.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends ItemAction> ItemActionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends ItemAction> ItemActionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		ResourceLocation prefixedId = id.withPrefix(ItemActionType.PREFIX);
		return ActionTypes.register(prefixedId, Registry.register(NeoApoliRegistries.ITEM_ACTION_TYPE, prefixedId, new ItemActionType<>(mapCodec, streamCodec)));
	}

}
