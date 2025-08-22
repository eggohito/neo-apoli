package io.github.eggohito.neo_apoli.power.misc;

import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.util.PriorityPhase;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public interface Prioritized<P extends Prioritized<P>> extends Comparable<P> {

	@Override
	default int compareTo(@NotNull P that) {
		return Integer.compare(this.getPriority(), that.getPriority());
	}

	default boolean inPriorityPhase(PriorityPhase phase) {
		return phase.test(this.getPriority());
	}

	int getPriority();

	final class CallInstance<I extends Power.Instance<?> & Prioritized<I>> {

		private final Int2ObjectMap<List<I>> buckets = new Int2ObjectOpenHashMap<>();

		@Getter
		private int minPriority = Integer.MAX_VALUE;
		@Getter
		private int maxPriority = Integer.MIN_VALUE;

		public static <I extends Power.Instance<?> & Prioritized<I>> CallInstance<I> create(Entity entity, Class<I> implClass, Predicate<I> implFilter) {

			CallInstance<I> instance = new CallInstance<>();
			instance.add(entity, implClass, implFilter);

			return instance;

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

		public void add(I instance) {

			int priority = instance.getPriority();
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
