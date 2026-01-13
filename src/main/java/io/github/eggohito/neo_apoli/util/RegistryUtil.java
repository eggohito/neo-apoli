package io.github.eggohito.neo_apoli.util;

import com.mojang.datafixers.util.Either;
import io.github.eggohito.neo_apoli.util.context.Context;
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

	public static <T> void validateKey(Context.Validator validator, ResourceKey<T> key) {
		validate(validator, Either.left(key));
	}

	public static <T> void validateTag(Context.Validator validator, TagKey<T> tag) {
		validate(validator, Either.right(tag));
	}

	public static <T> void validate(Context.Validator validator, Either<ResourceKey<T>, TagKey<T>> keyOrTag) {

		ResourceKey<? extends Registry<T>> registryRef = keyOrTag.map(ResourceKey::registryKey, TagKey::registry);
		if (!validator.hasLookupProvider()) {
			validator.report("Couldn't access registry " + registryRef + "!");
		}

		else {

			HolderLookup.RegistryLookup<T> lookup = validator.getLookupProviderUnsafe()
				.lookup(registryRef)
				.orElse(null);

			if (lookup == null) {
				validator.report("Couldn't find registry " + registryRef + "!");
			}

			else if (keyOrTag.map(lookup::get, lookup::get).isEmpty()) {
				validator.report(keyOrTag.map(ResourceKey::toString, TagKey::toString) + " doesn't exist!");
			}

		}

	}

}
