package io.github.eggohito.neo_apoli.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

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
		void beforeReload(ResourceManager manager, ProfilerFiller profiler);
	}

	@FunctionalInterface
	public interface After {
		void afterReload(ResourceManager manager, ProfilerFiller profiler);
	}

}
