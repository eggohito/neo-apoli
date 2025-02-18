package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.IndexedStack;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;

import java.util.List;

public class GiveItemsPower extends Power {

	public static final MapCodec<GiveItemsPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonFields(instance).and(
		IndexedStack.LIST_CODEC.fieldOf("stacks").forGetter(GiveItemsPower::getIndexedStacks)
	).apply(instance, GiveItemsPower::new));

	public static final PacketCodec<RegistryByteBuf, GiveItemsPower> PACKET_CODEC = createCommonPacketCodec(
		(buf, power) -> IndexedStack.LIST_PACKET_CODEC.encode(buf, power.getIndexedStacks()),
		(buf, metadata) -> new GiveItemsPower(metadata, IndexedStack.LIST_PACKET_CODEC.decode(buf))
	);

	private final List<IndexedStack> indexedStacks;
	private int stuff = Random.create().nextInt();

	public GiveItemsPower(Metadata metadata, List<IndexedStack> indexedStacks) {
		super(metadata);
		this.indexedStacks = indexedStacks;
	}

	@Override
	public PowerType<? extends Power> getType() {
		return PowerTypes.GIVE_ITEMS;
	}

	@Override
	public void onGained(Entity entity) {

		if (!(entity.getWorld() instanceof ServerWorld serverWorld)) {
			return;
		}

		for (IndexedStack indexedStack : indexedStacks) {

			ItemStack stack = indexedStack.stack().copy();
			IntList slots = indexedStack.slotIds().orElseGet(IntArrayList::new);

			boolean given = slots.intStream()
				.boxed()
				.map(entity::getStackReference)
				.anyMatch(stackReference -> stackReference.set(stack));

			if (!given) {

				if (entity instanceof PlayerEntity player) {
					player.getInventory().offerOrDrop(stack);
				}

				else {
					entity.dropStack(serverWorld, stack);
				}

			}

		}

	}


	@Override
	public <I> DataResult<I> encodeData(RegistryOps<I> registryOps) {
		return PrimitiveCodec.INT.encodeStart(registryOps, stuff);
	}

	@Override
	public <I> void decodeData(RegistryOps<I> registryOps, I data) {
		this.stuff = PrimitiveCodec.INT.parse(registryOps, data).getOrThrow();
	}

	public List<IndexedStack> getIndexedStacks() {
		return indexedStacks;
	}

}
