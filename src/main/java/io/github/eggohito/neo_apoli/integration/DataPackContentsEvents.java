package io.github.eggohito.neo_apoli.integration;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.DataPackContents;

public final class DataPackContentsEvents {

	/**
	 * 	Events used to inject where the pending tag loads of a data pack content is applied. Pending tag loads are applied
	 * 	after reload or after a server finishes initializing its data packs.
	 */
	public static final class PendingTagLoadEvents {

		public static final Event<Before> BEFORE = EventFactory.createArrayBacked(
			Before.class,
			callbacks -> dataPackContents -> {

				for (var callback: callbacks) {
					callback.onBeforeLoad(dataPackContents);
				}

			}
		);

		public static final Event<After> AFTER = EventFactory.createArrayBacked(
			After.class,
			callbacks -> dataPackContents -> {

				for (var callback: callbacks) {
					callback.onAfterLoad(dataPackContents);
				}

			}
		);

		@FunctionalInterface
		public interface Before {
			void onBeforeLoad(DataPackContents dataPackContents);
		}

		@FunctionalInterface
		public interface After {
			void onAfterLoad(DataPackContents dataPackContents);
		}

	}

}
