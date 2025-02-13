package io.github.eggohito.neo_apoli.event;

import com.google.gson.JsonElement;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.util.PowerIdentifier;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;

public final class PowerLoadingEvents {

	public static final Event<Before> BEFORE = EventFactory.createArrayBacked(
		Before.class,
		callbacks -> (id, packData, directoryPath, registryOps) -> {

			for (var callback : callbacks) {
				callback.beforeLoad(id, packData, directoryPath, registryOps);
			}

		}
	);

	public static final Event<After> AFTER = EventFactory.createArrayBacked(
		After.class,
		callbacks -> (id, power, packData, registryOps) -> {

			for (var callback : callbacks) {
				callback.afterLoad(id, power, packData, registryOps);
			}

		}
	);

	public interface Before {
		void beforeLoad(Identifier id, PowerManager.PackData packData, String directoryPath, RegistryOps<JsonElement> registryOps);
	}

	public interface After {
		void afterLoad(PowerIdentifier id, Power power, PowerManager.PackData packData, RegistryOps<JsonElement> registryOps);
	}

}
