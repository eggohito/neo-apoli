package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.IndexedStack;
import io.github.eggohito.neo_apoli.util.InventoryUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class GiveItemsPower extends Power {

	public static final MapCodec<GiveItemsPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonFields(instance)
		.and(IndexedStack.LIST_CODEC.fieldOf("stacks").forGetter(GiveItemsPower::getIndexedStacks))
		.and(Codec.BOOL.optionalFieldOf("recurrent", false).forGetter(GiveItemsPower::isRecurrent))
		.apply(instance, GiveItemsPower::new));

	public static final PacketCodec<RegistryByteBuf, GiveItemsPower> PACKET_CODEC = createCommonPacketCodec(
		(buf, power) -> {
			IndexedStack.LIST_PACKET_CODEC.encode(buf, power.getIndexedStacks());
			buf.writeBoolean(power.isRecurrent());
		},
		(buf, properties) -> new GiveItemsPower(properties,
			IndexedStack.LIST_PACKET_CODEC.decode(buf),
			buf.readBoolean()
		)
	);

	private final List<IndexedStack> indexedStacks;
	private final boolean recurrent;

	public GiveItemsPower(Properties properties, List<IndexedStack> indexedStacks, boolean recurrent) {
		super(properties);
		this.indexedStacks = indexedStacks;
		this.recurrent = recurrent;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.GIVE_ITEMS;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	public static class Impl extends Power.Impl<GiveItemsPower> {

		protected Impl(@NotNull Entity holder, @NotNull GiveItemsPower power) {
			super(holder, power);
		}

		@Override
		public void onGranted() {
			this.give();
		}

		@Override
		public void onRespawn() {

			if (power.isRecurrent()) {
				this.give();
			}

		}

		public void give() {

			if (holder.getWorld().isClient()) {
				return;
			}

			loopingStacks:
			for (IndexedStack indexedStack : power.getIndexedStacks()) {

				ItemStack stack = indexedStack.stack().copy();
				IntList slots = indexedStack.slotIds().orElseGet(IntArrayList::new);

				for (int slot : slots) {

					StackReference stackReference = holder.getStackReference(slot);

					if (stackReference.get().isEmpty() && stackReference.set(stack)) {
						continue loopingStacks;
					}

				}

				if (holder instanceof PlayerEntity player) {
					player.getInventory().offerOrDrop(stack);
				}

				else {
					InventoryUtil.dropItem(holder, stack, true, false, 0);
				}

			}

		}

	}

}
