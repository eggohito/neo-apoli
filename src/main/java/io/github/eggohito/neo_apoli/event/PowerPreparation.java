package io.github.eggohito.neo_apoli.event;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import io.github.eggohito.neo_apoli.resource.json.JsonWithSource;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;

public interface PowerPreparation {

	Event<PowerPreparation> EVENT = EventFactory.createArrayBacked(
		PowerPreparation.class,
		callbacks -> (id, jsonWithSource, directoryPath, registryOps) -> {

			for (var callback : callbacks) {
				callback.prepare(id, jsonWithSource, directoryPath, registryOps);
			}

		}
	);

	void prepare(ResourceLocation id, JsonWithSource jsonWithSource, String directoryPath, DynamicOps<JsonElement> ops);

}
