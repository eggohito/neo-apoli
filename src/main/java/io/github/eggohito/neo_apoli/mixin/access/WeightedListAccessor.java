package io.github.eggohito.neo_apoli.mixin.access;

import net.minecraft.util.collection.WeightedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(WeightedList.class)
public interface WeightedListAccessor<E> {

	@Accessor
	List<WeightedList.Entry<E>> getEntries();

}
