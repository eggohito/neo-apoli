package io.github.eggohito.neo_apoli.util.services;

import io.github.eggohito.neo_apoli.NeoApoli;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

public final class Services {

	public static <T> T load(Class<T> serviceClass) {

		ServiceLoader<T> loader = ServiceLoader.load(serviceClass, Services.class.getClassLoader());
		Iterator<T> iterator = loader.iterator();

		while (iterator.hasNext()) {

			try {
				return iterator.next();
			}

			catch (ServiceConfigurationError error) {
				NeoApoli.LOGGER.debug(error.getMessage(), error);
			}

		}

		throw new IllegalStateException("Couldn't load service for " + serviceClass.getName());

	}

}
