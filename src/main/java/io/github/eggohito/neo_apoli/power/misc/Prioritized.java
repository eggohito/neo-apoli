package io.github.eggohito.neo_apoli.power.misc;

import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.util.PriorityPhase;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import lombok.Getter;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public interface Prioritized<P extends Power & Prioritized<P>> extends Comparable<P> {

	@Override
	default int compareTo(@NotNull P that) {
		return Integer.compare(this.getPriority(), that.getPriority());
	}

	default boolean inPriorityPhase(PriorityPhase phase) {
		return phase.test(this.getPriority());
	}

	int getPriority();

	final class InstanceCollection<I extends Power.Instance<? extends Prioritized<?>>> implements Iterable<I> {

		private final Int2ObjectMap<List<I>> buckets = new Int2ObjectAVLTreeMap<>(Comparator.reverseOrder());

		@Getter
		private int minPriority = Integer.MAX_VALUE;
		@Getter
		private int maxPriority = Integer.MIN_VALUE;

		public InstanceCollection(Entity entity, Class<I> instanceClass, Predicate<I> instanceFilter) {
			this.add(entity, instanceClass, instanceFilter);
		}

		public InstanceCollection(Entity entity, Class<I> instanceClass) {
			this.add(entity, instanceClass, instance -> true);
		}

		public InstanceCollection() {

		}

		@NotNull
		@Override
		public Iterator<I> iterator() {

			ObjectCollection<I> flattened = new ObjectArrayList<>();
			buckets.values().forEach(flattened::addAll);

			return flattened.iterator();

		}

		public List<I> getInstances(int priority) {
			return buckets.getOrDefault(priority, new ObjectArrayList<>());
		}

		public boolean hasInstances(int priority) {
			return buckets.containsKey(priority);
		}

		public boolean isEmpty() {
			return buckets.isEmpty();
		}

		public <U extends I> void add(Entity holder, @NotNull Class<U> instanceClass, @NotNull Predicate<U> instanceFilter) {

			PowersComponent.forEach(holder, (reference, instance, sources) -> {

				if (instanceClass.isInstance(instance)) {

					U casted = instanceClass.cast(instance);

					if (instanceFilter.test(casted)) {
						this.add(casted);
					}

				}

			});

		}

		private void add(I instance) {

			int priority = instance.getPower().getPriority();
			buckets.computeIfAbsent(priority, i -> new ObjectArrayList<>()).add(instance);

			if (priority < minPriority) {
				this.minPriority = priority;
			}

			if (priority > maxPriority) {
				this.maxPriority = priority;
			}

		}

	}

}
