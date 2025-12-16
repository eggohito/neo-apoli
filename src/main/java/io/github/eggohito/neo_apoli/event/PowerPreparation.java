package io.github.eggohito.neo_apoli.event;

import com.google.gson.JsonElement;
import io.github.eggohito.neo_apoli.resource.JsonReloadListener;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;

public interface PowerPreparation {

	Event<PowerPreparation> EVENT = EventFactory.createArrayBacked(
		PowerPreparation.class,
		callbacks -> (id, elementWithSource, directoryPath, registryOps) -> {

			for (var callback : callbacks) {
				callback.prepare(id, elementWithSource, directoryPath, registryOps);
			}

		}
	);

	void prepare(ResourceLocation id, JsonReloadListener.ObjectElementWithSource elementWithSource, String directoryPath, RegistryOps<JsonElement> ops);

}
