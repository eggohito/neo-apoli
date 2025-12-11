package io.github.eggohito.neo_apoli.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.ReloadableServerResources;

public final class ReloadableServerResourcesEvents {

	/**
	 * 	Events used to inject where the pending loading tags of data packs are applied. Pending loading tags are applied
	 * 	after reload or after a server finishes initializing its data packs.
	 */
	public static final class RegistryTagUpdate {

		public static final Event<Before> BEFORE = EventFactory.createArrayBacked(
			Before.class,
			callbacks -> resources -> {

				for (var callback: callbacks) {
					callback.onBeforeUpdate(resources);
				}

			}
		);

		public static final Event<After> AFTER = EventFactory.createArrayBacked(
			After.class,
			callbacks -> resources -> {

				for (var callback: callbacks) {
					callback.onAfterUpdate(resources);
				}

			}
		);

		@FunctionalInterface
		public interface Before {
			void onBeforeUpdate(ReloadableServerResources resources);
		}

		@FunctionalInterface
		public interface After {
			void onAfterUpdate(ReloadableServerResources resources);
		}

	}

}
