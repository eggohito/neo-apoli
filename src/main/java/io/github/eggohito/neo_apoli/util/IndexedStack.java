package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.inventory.SlotRanges;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public record IndexedStack(ItemStack stack, Optional<SlotRange> slotRange) {

	public static final MapCodec<IndexedStack> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		MapCodec.assumeMapUnsafe(ItemStack.OPTIONAL_CODEC).forGetter(IndexedStack::stack),
		SlotRanges.CODEC.optionalFieldOf("slot").forGetter(IndexedStack::slotRange)
	).apply(instance, IndexedStack::new));

	public static final Codec<List<IndexedStack>> LIST_CODEC = CODEC.codec().listOf(1, Integer.MAX_VALUE);

	public static final StreamCodec<RegistryFriendlyByteBuf, IndexedStack> STREAM_CODEC = StreamCodec.composite(
		ItemStack.OPTIONAL_STREAM_CODEC, IndexedStack::stack,
		ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8.map(SlotRanges::nameToIds, StringRepresentable::getSerializedName)), IndexedStack::slotRange,
		IndexedStack::new
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, List<IndexedStack>> LIST_STREAM_CODEC = ByteBufCodecs.collection(ObjectArrayList::new, STREAM_CODEC);

	public Optional<IntList> slotIds() {
		return slotRange().map(SlotRange::slots);
	}

}
