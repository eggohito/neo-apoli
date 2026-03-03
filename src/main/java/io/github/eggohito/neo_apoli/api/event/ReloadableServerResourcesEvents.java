package io.github.eggohito.neo_apoli.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.ReloadableServerResources;

public final class ReloadableServerResourcesEvents {

	/**
	 *  An event invoked <b>after</b> the postponed tags of data packs are applied. Postponed tags are applied after
	 *  a reload or after a server finishes initializing its data packs.
	 */
	public static final Event<AfterLoad> AFTER_LOAD = EventFactory.createArrayBacked(
		AfterLoad.class,
		callbacks -> resources -> {

			for (var callback : callbacks) {
				callback.afterLoad(resources);
			}

		}
	);

	public interface AfterLoad {
		void afterLoad(ReloadableServerResources resources);
	}

}
