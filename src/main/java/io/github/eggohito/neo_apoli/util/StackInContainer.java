package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record StackInContainer(ItemStack stack, int slot) {

	public static final Codec<StackInContainer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ItemStack.MAP_CODEC.forGetter(StackInContainer::stack),
		Codec.INT.fieldOf("slot").forGetter(StackInContainer::slot)
	).apply(instance, StackInContainer::new));

	public static final Codec<List<StackInContainer>> LIST_CODEC = CODEC.listOf();

}
