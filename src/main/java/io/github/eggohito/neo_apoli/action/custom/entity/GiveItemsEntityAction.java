package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.custom.item.ItemAction;
import io.github.eggohito.neo_apoli.action.custom.item.NothingItemAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.IndexedStack;
import io.github.eggohito.neo_apoli.util.InventoryUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record GiveItemsEntityAction(ItemAction itemAction, List<IndexedStack> stacks) implements EntityAction {

	private static final ContextKeySet ACTION_PARAMS = new ContextKeySet.Builder()
		.required(NeoApoliContextParams.SLOT_ACCESS)
		.required(NeoApoliContextParams.ITEM_STACK)
		.build();

	public static final MapCodec<GiveItemsEntityAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ItemAction.CODEC.optionalFieldOf("item_action", NothingItemAction.INSTANCE).forGetter(GiveItemsEntityAction::itemAction),
		IndexedStack.LIST_CODEC.fieldOf("stacks").forGetter(GiveItemsEntityAction::stacks)
	).apply(instance, GiveItemsEntityAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, GiveItemsEntityAction> STREAM_CODEC = StreamCodec.composite(
		ItemAction.STREAM_CODEC, GiveItemsEntityAction::itemAction,
		IndexedStack.LIST_STREAM_CODEC, GiveItemsEntityAction::stacks,
		GiveItemsEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.GIVE_ITEMS;
	}

	@Override
	public void execute(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters()) || !(context.level() instanceof ServerLevel serverLevel)) {
			return;
		}

		Entity entity = context.getRequired(NeoApoliContextParams.THIS_ENTITY);

		loopStacks:
		for (var indexedStack : stacks()) {

			SlotAccess stackAccess = InventoryUtil.createSingletonSlot(indexedStack.stack());
			Context itemContext = new Context.Builder(context)
				.withRequired(NeoApoliContextParams.SLOT_ACCESS, stackAccess)
				.withRequired(NeoApoliContextParams.ITEM_STACK, stackAccess.get())
				.build(serverLevel);

			itemAction().execute(itemContext.forChild(".item_action"));

			ItemStack stack = stackAccess.get();
			IntList slotIds = indexedStack.slotIds().orElseGet(IntArrayList::new);

			for (var slotId : slotIds) {

				SlotAccess slotAccess = entity.getSlot(slotId);
				ItemStack slotStack = slotAccess.get();

				if (slotStack.isEmpty() && slotAccess.set(stack)) {
					continue loopStacks;
				}

				else if (ItemStack.matches(slotStack, stack) && slotStack.getCount() < slotStack.getMaxStackSize()) {

					int insertAmount = Math.min(slotStack.getMaxStackSize() - slotStack.getCount(), stack.getCount());

					slotStack.grow(insertAmount);
					stack.shrink(insertAmount);

					if (stack.isEmpty()) {
						continue loopStacks;
					}

				}

			}

			if (entity instanceof Player player) {
				player.getInventory().placeItemBackInInventory(stack);
			}

			else {
				InventoryUtil.dropItem(serverLevel, entity, stack, false, false, 0);
			}

		}

	}

	@Override
	public void validate(Context.Validator validator) {
		EntityAction.super.validate(validator);
		itemAction().validate(validator
			.withAdditionalKeysFromSets(ACTION_PARAMS)
			.forChild(".item_action"));
	}

}
