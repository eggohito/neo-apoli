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

	final class CallInstance<I extends Power.Impl<?> & Prioritized<I>> {

		private final Int2ObjectMap<List<I>> buckets = new Int2ObjectOpenHashMap<>();

		@Getter
		private int minPriority = Integer.MAX_VALUE;
		@Getter
		private int maxPriority = Integer.MIN_VALUE;

		public static <I extends Power.Impl<?> & Prioritized<I>> CallInstance<I> create(Entity entity, Class<I> implClass, Predicate<I> implFilter) {

			CallInstance<I> instance = new CallInstance<>();
			instance.add(entity, implClass, implFilter);

			return instance;

		}

		public List<I> getImpls(int priority) {
			return buckets.getOrDefault(priority, new ObjectArrayList<>());
		}

		public boolean hasImpls(int priority) {
			return buckets.containsKey(priority);
		}

		public boolean isEmpty() {
			return buckets.isEmpty();
		}

		public <U extends I> void add(Entity holder, @NotNull Class<U> implClass, @NotNull Predicate<U> implFilter) {

			PowersComponent.forEach(holder, (reference, impl, sources) -> {

				if (implClass.isInstance(impl)) {

					U casted = implClass.cast(impl);

					if (implFilter.test(casted)) {
						this.add(casted);
					}

				}

			});

		}

		public void add(I impl) {

			int priority = impl.getPriority();
			buckets.computeIfAbsent(priority, i -> new ObjectArrayList<>()).add(impl);

			if (priority < minPriority) {
				this.minPriority = priority;
			}

			if (priority > maxPriority) {
				this.maxPriority = priority;
			}

		}

	}

}
