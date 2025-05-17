package io.github.eggohito.neo_apoli.event;

import com.google.gson.JsonElement;
import io.github.eggohito.neo_apoli.resource.MultiDirectoryResourceReloader;
import io.github.eggohito.neo_apoli.util.PowerEntry;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;

public final class PowerLoadingEvents {

	public static final Event<Before> BEFORE = EventFactory.createArrayBacked(
		Before.class,
		callbacks -> (id, dataEntry, directoryPath, registryOps) -> {

			for (var callback : callbacks) {
				callback.beforeLoad(id, dataEntry, directoryPath, registryOps);
			}

		}
	);

	public static final Event<After> AFTER = EventFactory.createArrayBacked(
		After.class,
		callbacks -> (powerEntry, dataEntry, registryOps) -> {

			for (var callback : callbacks) {
				callback.afterLoad(powerEntry, dataEntry, registryOps);
			}

		}
	);

	public interface Before {
		void beforeLoad(Identifier id, MultiDirectoryResourceReloader.Entry dataEntry, String directoryPath, RegistryOps<JsonElement> registryOps);
	}

	public interface After {
		void afterLoad(PowerEntry<?> powerEntry, MultiDirectoryResourceReloader.Entry dataEntry, RegistryOps<JsonElement> registryOps);
	}

}
