package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import io.github.eggohito.neo_apoli.mixin.access.RegistryAccessor;
import io.github.eggohito.neo_apoli.util.alias.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.Objects;
import java.util.function.Function;

public final class RegistryUtil {

	public static <T> Codec<RegistryEntry.Reference<T>> createAliasedReferenceCodec(RegistryFixedAlias<T> registryAlias) {

		Registry<T> registry = registryAlias.getRegistry();
		Codec<RegistryEntry.Reference<T>> codec = Identifier.CODEC.comapFlatMap(registryAlias::resolve, reference -> reference.registryKey().getValue());

		return Codecs.withLifecycle(
			codec,
			entry -> registry.getEntryInfo(entry.registryKey())
				.map(RegistryEntryInfo::lifecycle)
				.orElseGet(Lifecycle::experimental)
		);

	}

	@SuppressWarnings("ReferenceToMixin")
	public static <T> Codec<RegistryEntry<T>> createAliasedEntryCodec(RegistryFixedAlias<T> aliases) {
		return createAliasedReferenceCodec(aliases).flatComapMap(
			Function.identity(),
			entry -> ((RegistryAccessor) aliases.getRegistry()).callValidateReference(entry)
		);
	}

	@SuppressWarnings("ReferenceToMixin")
	public static <T> Codec<T> createAliasedCodec(RegistryFixedAlias<T> aliases) {
		return createAliasedReferenceCodec(aliases).flatComapMap(
			RegistryEntry.Reference::value,
			value -> ((RegistryAccessor) aliases.getRegistry()).callValidateReference(aliases.getRegistry().getEntry(value))
		);
	}

	/**
	 * 	<b>Use {@link #createAliasedCodec(RegistryFixedAlias)} instead</b>
	 */
	@Deprecated
	public static <T> Codec<T> createAliasedCodec(Registry<T> registry, IdentifierAlias aliases) {

		Codec<RegistryEntry.Reference<T>> entryCodec = Identifier.CODEC.comapFlatMap(
			id -> registry.getEntry(aliases.resolve(id, registry::containsId))
				.map(DataResult::success)
				.orElseGet(() -> DataResult.error(() -> "Unknown registry key in " + registry.getKey() + ": " + id)),
			entry -> entry.registryKey().getValue()
		);

		Codec<RegistryEntry.Reference<T>> lifecycledEntryCodec = Codecs.withLifecycle(
			entryCodec,
			entry -> registry.getEntryInfo(entry.registryKey())
				.map(RegistryEntryInfo::lifecycle)
				.orElse(Lifecycle.experimental())
		);

		return lifecycledEntryCodec.flatComapMap(
			RegistryEntry.Reference::value,
			t -> registry.getEntry(t) instanceof RegistryEntry.Reference<T> reference
				? DataResult.success(reference)
				: DataResult.error(() -> "Unregistered holder in " + registry.getKey() + ": " + registry.getEntry(t))
		);

	}

	public static <T> Identifier getId(Registry<T> registry, T obj) {
		return Objects.requireNonNull(registry.getId(obj));
	}

	public static <T> String getIdNamespace(Registry<T> registry, T obj) {
		return getId(registry, obj).getNamespace();
	}

	public static <T> String getIdPath(Registry<T> registry, T obj) {
		return getId(registry, obj).getPath();
	}

	public static <T> void validateTag(ContextAware.ErrorReporter reporter, TagKey<T> tag) {

		RegistryKey<? extends Registry<T>> registryRef = tag.registryRef();
		RegistryWrapper.WrapperLookup wrapperLookup = reporter.getWrapperLookup().orElse(null);

		if (wrapperLookup == null) {
			reporter.report("Couldn't access registry " + registryRef + "!");
		}

		else {

			RegistryWrapper.Impl<T> registryWrapper = wrapperLookup
				.getOptional(tag.registryRef())
				.orElse(null);

			if (registryWrapper == null) {
				reporter.report("Couldn't find registry \"" + registryRef + "\"!");
			}

			else if (registryWrapper.getOptional(tag).isEmpty()) {
				reporter.report(tag + " doesn't exist!");
			}

		}

	}

}
