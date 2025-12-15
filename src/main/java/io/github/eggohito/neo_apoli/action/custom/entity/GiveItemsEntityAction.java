package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.custom.item.ItemAction;
import io.github.eggohito.neo_apoli.action.custom.item.NothingItemAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.IndexedStack;
import io.github.eggohito.neo_apoli.util.InventoryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextKeySetHelper;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeySets;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public record GiveItemsEntityAction(ItemAction itemAction, List<IndexedStack> stacks) implements EntityAction {

	public static final MapCodec<GiveItemsEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ItemAction.CODEC.optionalFieldOf("item_action", new NothingItemAction()).forGetter(GiveItemsEntityAction::itemAction),
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

		Level world = context.getLevel();
		Entity entity = context.nullable(NeoApoliContextKeys.THIS_ENTITY);

		if (!(world instanceof ServerLevel serverLevel) || entity == null) {
			return;
		}

		loopingStacks:
		for (var indexedStack : stacks()) {

			SlotAccess stackAccess = InventoryUtil.createSingletonSlot(indexedStack.stack());
			Context itemContext = new Context.Builder(context)
				.withKeySet(ContextKeySetHelper.merge(context.getKeySet(), NeoApoliContextKeySets.ITEM))
				.add(NeoApoliContextKeys.STACK_REFERENCE, stackAccess)
				.add(NeoApoliContextKeys.ITEM_STACK, stackAccess.get())
				.build(serverLevel);

			itemAction().execute(itemContext.forChild(".item_action"));

			ItemStack stack = stackAccess.get();
			IntList slotIds = indexedStack.slotIds().orElseGet(IntArrayList::new);

			for (var slotId : slotIds) {

				SlotAccess slot = entity.getSlot(slotId);
				ItemStack slotStack = slot.get();

				if (slotStack.isEmpty() && slot.set(stack)) {
					continue loopingStacks;
				}

				else if (ItemStack.matches(slotStack, stack) && slotStack.getCount() < slotStack.getMaxStackSize()) {

					int amountToGive = Math.min(slotStack.getMaxStackSize() - slotStack.getCount(), stack.getCount());

					slotStack.grow(amountToGive);
					stack.shrink(amountToGive);

					if (stack.isEmpty()) {
						continue loopingStacks;
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
	public void validate(ProblemReporter reporter) {
		EntityAction.super.validate(reporter);
		itemAction().validate(reporter
			.withKeySet(ContextKeySetHelper.merge(reporter.getKeySet(), NeoApoliContextKeySets.ITEM))
			.forChild(".item_action"));
	}

}
