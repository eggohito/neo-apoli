package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.IndexedStack;
import io.github.eggohito.neo_apoli.util.InventoryUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public class GiveItemsPower extends Power {

	public static final MapCodec<GiveItemsPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonFields(instance)
		.and(IndexedStack.LIST_CODEC.fieldOf("stacks").forGetter(GiveItemsPower::getIndexedStacks))
		.and(PrimitiveCodec.BOOL.optionalFieldOf("recurrent", false).forGetter(GiveItemsPower::isRecurrent))
		.apply(instance, GiveItemsPower::new));

	public static final PacketCodec<RegistryByteBuf, GiveItemsPower> PACKET_CODEC = createCommonPacketCodec(
		(buf, power) -> {
			IndexedStack.LIST_PACKET_CODEC.encode(buf, power.getIndexedStacks());
			buf.writeBoolean(power.isRecurrent());
		},
		(buf, properties) -> {

			List<IndexedStack> indexedStacks = IndexedStack.LIST_PACKET_CODEC.decode(buf);
			boolean recurrent = buf.readBoolean();

			return new GiveItemsPower(properties, indexedStacks, recurrent);

		}
	);

	private final List<IndexedStack> indexedStacks;
	private final boolean recurrent;

	public GiveItemsPower(Properties properties, List<IndexedStack> indexedStacks, boolean recurrent) {
		super(properties);
		this.indexedStacks = indexedStacks;
		this.recurrent = recurrent;
	}

	@Override
	public PowerType<? extends Power> getType() {
		return PowerTypes.GIVE_ITEMS;
	}

	@Override
	public void onRespawn(PlayerEntity holder) {

		if (this.isRecurrent()) {
			this.give(holder);
		}

	}

	@Override
	public void onGranted(Entity entity) {
		this.give(entity);
	}

	public List<IndexedStack> getIndexedStacks() {
		return indexedStacks;
	}

	public boolean isRecurrent() {
		return recurrent;
	}

	public void give(Entity entity) {

		if (entity.getWorld().isClient()) {
			return;
		}

		for (IndexedStack indexedStack : getIndexedStacks()) {

			ItemStack stack = indexedStack.stack().copy();
			IntList slots = indexedStack.slotIds().orElseGet(IntArrayList::new);

			boolean given = false;
			for (int slot : slots) {
				given |= entity.getStackReference(slot).set(stack);
			}

			if (!given) {

				if (entity instanceof PlayerEntity player) {
					player.getInventory().offerOrDrop(stack);
				}

				else {
					InventoryUtil.dropItem(entity, stack, true, false, 0);
				}

			}

		}

	}

}
