package io.github.eggohito.neo_apoli.util;

import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Objects;

public final class RegistryUtil {

	public static <T> ResourceLocation getId(Registry<T> registry, T obj) {
		return Objects.requireNonNull(registry.getKey(obj));
	}

	public static <T> String getNamespace(Registry<T> registry, T obj) {
		return getId(registry, obj).getNamespace();
	}

	public static <T> String getPath(Registry<T> registry, T obj) {
		return getId(registry, obj).getPath();
	}

	public static <T> void validateEntry(ContextAware.ProblemReporter reporter, ResourceKey<T> key) {

		ResourceKey<? extends Registry<T>> registryRef = key.registryKey();
		HolderLookup.Provider lookupProvider = reporter.getHolderProvider().orElse(null);

		if (lookupProvider == null) {
			reporter.report("Couldn't access registry " + registryRef + "!");
		}

		else {

			HolderLookup.RegistryLookup<T> lookup = lookupProvider
				.lookup(registryRef)
				.orElse(null);

			if (lookup == null) {
				reporter.report("Couldn't find registry " + registryRef + "!");
			}

			else if (lookup.get(key).isEmpty()) {
				reporter.report(key + " doesn't exist!");
			}

		}

	}

	public static <T> void validateTag(ContextAware.ProblemReporter reporter, TagKey<T> tag) {

		ResourceKey<? extends Registry<T>> registryRef = tag.registry();
		HolderLookup.Provider lookupProvider
			= reporter.getHolderProvider().orElse(null);

		if (lookupProvider == null) {
			reporter.report("Couldn't access registry " + registryRef + "!");
		}

		else {

			HolderLookup.RegistryLookup<T> lookup = lookupProvider
				.lookup(registryRef)
				.orElse(null);

			if (lookup == null) {
				reporter.report("Couldn't find registry " + registryRef + "!");
			}

			else if (lookup.get(tag).isEmpty()) {
				reporter.report(tag + " doesn't exist!");
			}

		}

	}

}
