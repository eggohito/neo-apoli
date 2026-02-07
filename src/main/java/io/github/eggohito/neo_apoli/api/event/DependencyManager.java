package io.github.eggohito.neo_apoli.api.event;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;

public interface DependencyManager {

	Event<DependencyManager> ACTIONS = create();
	Event<DependencyManager> CONDITIONS = create();
	Event<DependencyManager> POWERS = create();
	Event<DependencyManager> GLOBAL_POWER_SETS = create();

	void add(ImmutableSet.Builder<ResourceLocation> dependencies);

	private static Event<DependencyManager> create() {
		return EventFactory.createArrayBacked(
			DependencyManager.class,
			callbacks -> dependencies -> {

				for (var callback : callbacks) {
					callback.add(dependencies);
				}

			}
		);
	}

}
