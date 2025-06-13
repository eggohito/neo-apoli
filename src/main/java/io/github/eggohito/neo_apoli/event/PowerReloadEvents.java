package io.github.eggohito.neo_apoli.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;

public final class PowerReloadEvents {

	public static final Event<Before> BEFORE = EventFactory.createArrayBacked(
		Before.class,
		callbacks -> (manager, profiler) -> {

			for (var callback : callbacks) {
				callback.beforeReload(manager, profiler);
			}

		}
	);

	public static final Event<After> AFTER = EventFactory.createArrayBacked(
		After.class,
		callbacks -> (manager, profiler) -> {

			for (var callback : callbacks) {
				callback.afterReload(manager, profiler);
			}

		}
	);

	@FunctionalInterface
	public interface Before {
		void beforeReload(ResourceManager manager, Profiler profiler);
	}

	@FunctionalInterface
	public interface After {
		void afterReload(ResourceManager manager, Profiler profiler);
	}

}
