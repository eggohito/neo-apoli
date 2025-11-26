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

	public static final ItemActionType<ChoiceItemAction> CHOICE = registerMetaInternal("choice", ChoiceItemAction.CODEC, ChoiceItemAction.STREAM_CODEC);
	public static final ItemActionType<ConditionalItemAction> CONDITIONAL = registerMetaInternal("conditional", ConditionalItemAction.CODEC, ConditionalItemAction.STREAM_CODEC);
	public static final ItemActionType<LoopItemAction> LOOP = registerMetaInternal("loop", LoopItemAction.CODEC, LoopItemAction.STREAM_CODEC);
	public static final ItemActionType<NothingItemAction> NOTHING = registerMetaInternal("nothing", NothingItemAction.CODEC, NothingItemAction.STREAM_CODEC);
	public static final ItemActionType<RandomChanceItemAction> RANDOM_CHANCE = registerMetaInternal("random_chance", RandomChanceItemAction.CODEC, RandomChanceItemAction.STREAM_CODEC);
	public static final ItemActionType<ReferenceItemAction> REFERENCE = registerMetaInternal("reference", ReferenceItemAction.CODEC, ReferenceItemAction.STREAM_CODEC);
	public static final ItemActionType<SequenceItemAction> SEQUENCE = registerMetaInternal("sequence", SequenceItemAction.CODEC, SequenceItemAction.STREAM_CODEC);
	public static final ItemActionType<WeightedItemAction> WEIGHTED = registerMetaInternal("weighted", WeightedItemAction.CODEC, WeightedItemAction.STREAM_CODEC);

	public static final ItemActionType<ConsumeItemAction> CONSUME = registerInternal("consume", ConsumeItemAction.CODEC, ConsumeItemAction.STREAM_CODEC);
	public static final ItemActionType<DamageItemAction> DAMAGE = registerInternal("damage", DamageItemAction.CODEC, DamageItemAction.STREAM_CODEC);
	public static final ItemActionType<ModifyItemAction> MODIFY = registerInternal("modify", ModifyItemAction.CODEC, ModifyItemAction.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends ItemAction> ItemActionType<C> registerMetaInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return registerMeta(NeoApoli.id(path), mapCodec, streamCodec);
	}

	private static <C extends ItemAction> ItemActionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends ItemAction> ItemActionType<C> registerMeta(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.ITEM_ACTION_TYPE, id, new ItemActionType<>(mapCodec, streamCodec));
	}

	public static <C extends ItemAction> ItemActionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		ResourceLocation prefixedId = id.withPrefix(ItemActionType.PREFIX);
		return ActionTypes.register(prefixedId, registerMeta(prefixedId, mapCodec, streamCodec));
	}

}
