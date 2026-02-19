package io.github.eggohito.neo_apoli.duck;

import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraft.util.RandomSource;

import java.util.Optional;

public interface WeightedExtension<E> {

	default Optional<ObjectIntPair<E>> neo_apoli$getRandomAndIndex(RandomSource random) {
		return Optional.empty();
	}

}
