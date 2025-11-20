package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.custom.item.ItemAction;
import io.github.eggohito.neo_apoli.action.custom.item.NothingItemAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.IndexedStack;
import io.github.eggohito.neo_apoli.util.InventoryUtil;
import io.github.eggohito.neo_apoli.util.context.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.List;

public record GiveItemsEntityAction(ItemAction itemAction, List<IndexedStack> stacks) implements EntityAction {

	public static final MapCodec<GiveItemsEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ItemAction.CODEC.optionalFieldOf("item_action", new NothingItemAction()).forGetter(GiveItemsEntityAction::itemAction),
		IndexedStack.LIST_CODEC.fieldOf("stacks").forGetter(GiveItemsEntityAction::stacks)
	).apply(instance, GiveItemsEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, GiveItemsEntityAction> PACKET_CODEC = PacketCodec.tuple(
		ItemAction.PACKET_CODEC, GiveItemsEntityAction::itemAction,
		IndexedStack.LIST_PACKET_CODEC, GiveItemsEntityAction::stacks,
		GiveItemsEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.GIVE_ITEMS;
	}

	@Override
	public void execute(Context context) {

		World world = context.getWorld();
		Entity entity = context.nullable(NeoApoliContextParameters.THIS_ENTITY);

		if (!(world instanceof ServerWorld serverWorld) || entity == null) {
			return;
		}

		loopingStacks:
		for (var indexedStack : stacks()) {

			StackReference stackReference = InventoryUtil.createStackReference(indexedStack.stack());
			ServerContext itemContext = new ServerContext.Builder(context)
				.withContextType(ContextTypeUtil.merge(context.getType(), NeoApoliContextTypes.ITEM))
				.add(NeoApoliContextParameters.STACK_REFERENCE, stackReference)
				.add(NeoApoliContextParameters.ITEM_STACK, stackReference.get())
				.build(serverWorld);

			itemAction().execute(itemContext.makeChild(".item_action"));

			ItemStack stack = stackReference.get();
			IntList slots = indexedStack.slotIds().orElseGet(IntArrayList::new);

			for (var slot : slots) {

				StackReference slotReference = entity.getStackReference(slot);
				ItemStack slotStack = slotReference.get();

				if (slotStack.isEmpty() && slotReference.set(stack)) {
					continue loopingStacks;
				}

				else if (ItemStack.areEqual(slotStack, stack) && slotStack.getCount() < slotStack.getMaxCount()) {

					int amountToGive = Math.min(slotStack.getMaxCount() - slotStack.getCount(), stack.getCount());

					slotStack.increment(amountToGive);
					stack.decrement(amountToGive);

					if (stack.isEmpty()) {
						continue loopingStacks;
					}

				}

			}

			if (entity instanceof PlayerEntity player) {
				player.getInventory().offerOrDrop(stack);
			}

			else {
				InventoryUtil.dropItem(serverWorld, entity, stack, false, false, 0);
			}

		}

	}

	@Override
	public void validate(ErrorReporter reporter) {
		EntityAction.super.validate(reporter);
		itemAction().validate(reporter
			.withContextType(ContextTypeUtil.merge(reporter.getContextType(), NeoApoliContextTypes.ITEM))
			.makeChild(".item_action"));
	}

}
