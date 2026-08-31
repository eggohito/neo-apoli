package io.github.eggohito.neo_apoli.mixin.impl.misc.weighted_extension;

import io.github.eggohito.neo_apoli.duck.WeightedExtension;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(WeightedList.class)
public abstract class WeightedListMixin<E> implements WeightedExtension<E> {

	@Shadow
	@Final
	@Nullable
	private WeightedList.@Nullable Selector<E> selector;

	@Shadow
	@Final
	private int totalWeight;

	@Override
	public Optional<ObjectIntPair<E>> neo_apoli$getRandomAndIndex(RandomSource random) {

		if (this.selector == null) {
			return Optional.empty();
		}

		else {

			int index = random.nextInt(this.totalWeight);
			E element = this.selector.get(index);

			return Optional.of(new ObjectIntImmutablePair<>(element, index));

		}

	}

}
