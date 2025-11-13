package io.github.eggohito.neo_apoli.event;

import com.google.gson.JsonElement;
import io.github.eggohito.neo_apoli.power.PowerManager;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;

public interface PowerPreparation {

	Event<PowerPreparation> EVENT = EventFactory.createArrayBacked(
		PowerPreparation.class,
		callbacks -> (id, dataEntry, directoryPath, registryOps) -> {

			for (var callback : callbacks) {
				callback.prepare(id, dataEntry, directoryPath, registryOps);
			}

		}
	);

	void prepare(Identifier id, PowerManager.Entry entry, String directoryPath, RegistryOps<JsonElement> ops);

}
