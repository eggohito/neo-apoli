package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.inventory.SlotRange;
import net.minecraft.inventory.SlotRanges;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;

import java.util.List;
import java.util.Optional;

public record IndexedStack(ItemStack stack, Optional<SlotRange> slotRange) {

	public static final MapCodec<IndexedStack> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		MapCodec.assumeMapUnsafe(ItemStack.OPTIONAL_CODEC).forGetter(IndexedStack::stack),
		SlotRanges.CODEC.optionalFieldOf("slot").forGetter(IndexedStack::slotRange)
	).apply(instance, IndexedStack::new));

	public static final Codec<List<IndexedStack>> LIST_CODEC = CODEC.codec().listOf(1, Integer.MAX_VALUE);

	public static final PacketCodec<RegistryByteBuf, IndexedStack> PACKET_CODEC = PacketCodec.tuple(
		ItemStack.OPTIONAL_PACKET_CODEC, IndexedStack::stack,
		PacketCodecs.optional(PacketCodecs.STRING.xmap(SlotRanges::fromName, StringIdentifiable::asString)), IndexedStack::slotRange,
		IndexedStack::new
	);

	public static final PacketCodec<RegistryByteBuf, List<IndexedStack>> LIST_PACKET_CODEC = PacketCodecs.collection(ObjectArrayList::new, PACKET_CODEC);

	public Optional<IntList> slotIds() {
		return slotRange().map(SlotRange::getSlotIds);
	}

}
