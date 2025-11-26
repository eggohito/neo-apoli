package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import io.github.eggohito.neo_apoli.mixin.access.RegistryAccessor;
import io.github.eggohito.neo_apoli.util.alias.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;

import java.util.Objects;
import java.util.function.Function;

public final class RegistryUtil {

	public static <T> Codec<Holder.Reference<T>> createAliasedReferenceCodec(RegistryFixedAlias<T> registryAlias) {

		Registry<T> registry = registryAlias.getRegistry();
		Codec<Holder.Reference<T>> codec = ResourceLocation.CODEC.comapFlatMap(registryAlias::resolve, reference -> reference.key().location());

		return ExtraCodecs.overrideLifecycle(
			codec,
			entry -> registry.registrationInfo(entry.key())
				.map(RegistrationInfo::lifecycle)
				.orElseGet(Lifecycle::experimental)
		);

	}

	@SuppressWarnings("ReferenceToMixin")
	public static <T> Codec<Holder<T>> createAliasedEntryCodec(RegistryFixedAlias<T> aliases) {
		return createAliasedReferenceCodec(aliases).flatComapMap(
			Function.identity(),
			entry -> ((RegistryAccessor) aliases.getRegistry()).callSafeCastToReference(entry)
		);
	}

	@SuppressWarnings("ReferenceToMixin")
	public static <T> Codec<T> createAliasedCodec(RegistryFixedAlias<T> aliases) {
		return createAliasedReferenceCodec(aliases).flatComapMap(
			Holder.Reference::value,
			value -> ((RegistryAccessor) aliases.getRegistry()).callSafeCastToReference(aliases.getRegistry().wrapAsHolder(value))
		);
	}

	/**
	 * 	<b>Use {@link #createAliasedCodec(RegistryFixedAlias)} instead</b>
	 */
	@Deprecated
	public static <T> Codec<T> createAliasedCodec(Registry<T> registry, IdentifierAlias aliases) {

		Codec<Holder.Reference<T>> entryCodec = ResourceLocation.CODEC.comapFlatMap(
			id -> registry.get(aliases.resolve(id, registry::containsKey))
				.map(DataResult::success)
				.orElseGet(() -> DataResult.error(() -> "Unknown registry key in " + registry.key() + ": " + id)),
			entry -> entry.key().location()
		);

		Codec<Holder.Reference<T>> lifecycledEntryCodec = ExtraCodecs.overrideLifecycle(
			entryCodec,
			entry -> registry.registrationInfo(entry.key())
				.map(RegistrationInfo::lifecycle)
				.orElse(Lifecycle.experimental())
		);

		return lifecycledEntryCodec.flatComapMap(
			Holder.Reference::value,
			t -> registry.wrapAsHolder(t) instanceof Holder.Reference<T> reference
				? DataResult.success(reference)
				: DataResult.error(() -> "Unregistered holder in " + registry.key() + ": " + registry.wrapAsHolder(t))
		);

	}

	public static <T> ResourceLocation getId(Registry<T> registry, T obj) {
		return Objects.requireNonNull(registry.getKey(obj));
	}

	public static <T> String getIdNamespace(Registry<T> registry, T obj) {
		return getId(registry, obj).getNamespace();
	}

	public static <T> String getIdPath(Registry<T> registry, T obj) {
		return getId(registry, obj).getPath();
	}

	public static <T> void validateTag(ContextAware.ProblemReporter reporter, TagKey<T> tag) {

		ResourceKey<? extends Registry<T>> registryRef = tag.registry();
		HolderLookup.Provider wrapperLookup = reporter.getHolderProvider().orElse(null);

		if (wrapperLookup == null) {
			reporter.report("Couldn't access registry " + registryRef + "!");
		}

		else {

			HolderLookup.RegistryLookup<T> registryWrapper = wrapperLookup
				.lookup(tag.registry())
				.orElse(null);

			if (registryWrapper == null) {
				reporter.report("Couldn't find registry \"" + registryRef + "\"!");
			}

			else if (registryWrapper.get(tag).isEmpty()) {
				reporter.report(tag + " doesn't exist!");
			}

		}

	}

}
