package io.github.eggohito.neo_apoli.mixin.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import net.minecraft.world.entity.ai.behavior.ShufflingList;

@Mixin(ShufflingList.class)
public interface ShufflingListAccessor {

	@Accessor
	<E> List<ShufflingList.WeightedEntry<E>> getEntries();

}
