package io.github.eggohito.neo_apoli.util.services;

import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.util.ServiceLoader;
import java.util.function.Supplier;

public final class Services {

	private static final Logger LOGGER = LogUtils.getLogger();

	public static <T> T load(Class<T> serviceClass) {
		return ServiceLoader.load(serviceClass, Services.class.getClassLoader())
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Couldn't load service for " + serviceClass.getName()));
	}

	public static <M> Supplier<M> lazyLoadSideSpecific(Class<M> clientService, Supplier<M> serverInstance) {
		return Suppliers.memoize(
			() -> switch (FabricLoader.getInstance().getEnvironmentType()) {
				case CLIENT -> {
					LOGGER.debug("Client detected; attempting to load service for '{}'", clientService.getName());
					yield load(clientService);
				}
				case SERVER -> {

					M instance = serverInstance.get();
					LOGGER.debug("Dedicated server detected; instantiating '{}'", instance.getClass().getName());

					yield instance;

				}
			}
		);
	}

}
