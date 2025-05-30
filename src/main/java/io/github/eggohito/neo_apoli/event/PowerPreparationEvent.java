package io.github.eggohito.neo_apoli.event;

import com.google.gson.JsonElement;
import io.github.eggohito.neo_apoli.resource.JsonResourceReloader;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;

public final class PowerPreparationEvent {

	public static final Event<Preparation> EVENT = EventFactory.createArrayBacked(
		Preparation.class,
		callbacks -> (id, dataEntry, directoryPath, registryOps) -> {

			for (var callback : callbacks) {
				callback.prepare(id, dataEntry, directoryPath, registryOps);
			}

		}
	);

	public interface Preparation {
		void prepare(Identifier id, JsonResourceReloader.Entry dataEntry, String directoryPath, RegistryOps<JsonElement> registryOps);
	}

}
