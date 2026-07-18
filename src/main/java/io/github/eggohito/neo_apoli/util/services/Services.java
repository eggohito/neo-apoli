package io.github.eggohito.neo_apoli.util.services;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

public class Services {

	public static <T> T load(Class<T> serviceClass) {

		ServiceLoader<T> loader = ServiceLoader.load(serviceClass, Services.class.getClassLoader());
		Iterator<T> iterator = loader.iterator();

		RuntimeException exception = new IllegalStateException("Couldn't load service for " + serviceClass.getName());
		while (iterator.hasNext()) {

			try {
				return iterator.next();
			}

			catch (ServiceConfigurationError error) {
				exception.addSuppressed(error);
			}

		}

		throw exception;

	}

}
