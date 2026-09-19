package io.github.eggohito.neo_apoli.util.services;

import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.util.ServiceLoader;
import java.util.function.Supplier;

public final class NeoApoliServices {

	private static final Logger LOGGER = LogUtils.getLogger();

	public static <T> T load(Class<T> serviceClass) {
		return ServiceLoader.load(serviceClass, NeoApoliServices.class.getClassLoader())
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Couldn't load service for " + serviceClass.getName()));
	}

	public static <M> M loadPhysicalSideSpecific(Class<M> forClient, Supplier<M> forServer) {
		return switch (FabricLoader.getInstance().getEnvironmentType()) {
			case CLIENT -> {
				LOGGER.debug("Client detected; attempting to load service for '{}'", forClient.getName());
				yield load(forClient);
			}
			case SERVER -> {

				M instance = forServer.get();
				LOGGER.debug("Dedicated server detected; instantiating '{}'", instance.getClass().getName());

				yield instance;

			}
		};
	}

	public static <M> Supplier<M> deferredLoadPhysicalSideSpecific(Class<M> forClient, Supplier<M> forServer) {
		return Suppliers.memoize(() -> loadPhysicalSideSpecific(forClient, forServer));
	}

}
