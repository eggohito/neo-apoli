package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.meta.item.NothingItemAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.IndexedStack;
import io.github.eggohito.neo_apoli.util.InventoryUtil;
import io.github.eggohito.neo_apoli.util.context.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.List;

@EqualsAndHashCode
@Data
public final class GiveItemsEntityAction extends EntityAction {

	public static final MapCodec<GiveItemsEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ItemAction.CODEC.optionalFieldOf("item_action", new NothingItemAction()).forGetter(GiveItemsEntityAction::itemAction),
		IndexedStack.LIST_CODEC.fieldOf("stacks").forGetter(GiveItemsEntityAction::stacks)
	).apply(instance, GiveItemsEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, GiveItemsEntityAction> PACKET_CODEC = PacketCodec.tuple(
		ItemAction.PACKET_CODEC, GiveItemsEntityAction::itemAction,
		IndexedStack.LIST_PACKET_CODEC, GiveItemsEntityAction::stacks,
		GiveItemsEntityAction::new
	);

	private final ItemAction itemAction;
	private final List<IndexedStack> stacks;

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.GIVE_ITEMS;
	}

	@Override
	protected void impl(Context context) {

		World world = context.getWorld();
		Entity entity = context.required(ContextParameters.ENTITY);

		if (!(world instanceof ServerWorld serverWorld)) {
			return;
		}

		loopingStacks:
		for (IndexedStack slottedStack : stacks()) {

			StackReference givenStackReference = InventoryUtil.createStackReference(slottedStack.stack());
			Context itemContext = new ContextImpl.Builder(context)
				.withContextType(ContextTypeUtil.merge(context.getType(), ContextTypes.ITEM))
				.add(ContextParameters.STACK_REFERENCE, givenStackReference)
				.add(ContextParameters.ITEM_STACK, givenStackReference.get())
				.build(context.getWorld());

			itemAction().execute(itemContext.makeChild(".item_action"));

			ItemStack givenStack = givenStackReference.get();
			IntList slots = slottedStack.slotIds().orElseGet(IntArrayList::new);

			for (int slot : slots) {

				StackReference slotStackReference = entity.getStackReference(slot);
				ItemStack slotStack = slotStackReference.get();

				if (slotStack.isEmpty() && slotStackReference.set(givenStack)) {
					continue loopingStacks;
				}

				else if (ItemStack.areEqual(slotStack, givenStack) && slotStack.getCount() < slotStack.getMaxCount()) {

					int amountToGive = Math.min(slotStack.getMaxCount() - slotStack.getCount(), givenStack.getCount());

					slotStack.increment(amountToGive);
					givenStack.decrement(amountToGive);

					if (givenStack.isEmpty()) {
						continue loopingStacks;
					}

				}

			}

			if (entity instanceof PlayerEntity player) {
				player.getInventory().offerOrDrop(givenStack);
			}

			else {
				InventoryUtil.dropItem(serverWorld, entity, givenStack, false, false, 0);
			}

		}

	}

	@Override
	public void validate(ErrorReporter reporter) {

		super.validate(reporter);

		itemAction().validate(reporter
			.withContextType(ContextTypeUtil.merge(reporter.getContextType(), ContextTypes.ITEM))
			.makeChild(".item_action"));

	}

}
