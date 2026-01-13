package io.github.eggohito.neo_apoli.util.alias;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import io.github.eggohito.neo_apoli.exception.AliasAlreadyTakenException;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.ResourceLocationUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.function.Function;

public record RegistryFixedAlias<T>(Registry<T> registry, ResourceLocationAlias aliases) {

	public boolean hasNamespaceAlias(T t) {
		return aliases.getNamespaces().hasAlias(RegistryUtil.getNamespace(registry(), t));
	}

	public boolean hasPathAlias(T t) {
		return aliases.getPaths().hasAlias(RegistryUtil.getPath(registry(), t));
	}

	public boolean hasAlias(T t) {
		return aliases.hasAlias(RegistryUtil.getId(registry(), t));
	}

	public void addNamespaceAlias(String from, T to) {

		try {
			aliases.getNamespaces().addAlias(from, RegistryUtil.getNamespace(registry(), to));
		}

		catch (AliasAlreadyTakenException e) {
			throw new AliasAlreadyTakenException(e, registry().key());
		}

	}

	public void addPathAlias(String from, T to) {

		try {
			aliases.getPaths().addAlias(from, RegistryUtil.getPath(registry(), to));
		}

		catch (AliasAlreadyTakenException e) {
			throw new AliasAlreadyTakenException(e, registry().key());
		}

	}

	public void addAlias(ResourceLocation from, T to) {

		try {
			aliases.addAlias(from, RegistryUtil.getId(registry(), to));
		}

		catch (AliasAlreadyTakenException e) {
			throw new AliasAlreadyTakenException(e, registry().key());
		}

	}

	public DataResult<Holder.Reference<T>> resolve(ResourceLocation id) {
		return registry.get(aliases.resolve(id, registry::containsKey))
			.map(DataResult::success)
			.orElseGet(() -> DataResult.error(() -> "Unknown registry key in " + registry.key() + ": " + id));
	}

	public Codec<Holder.Reference<T>> createReferenceCodec(String defaultNamespace) {

		Codec<Holder.Reference<T>> codec = ResourceLocationUtil.codecWithDefaultNamespace(defaultNamespace).comapFlatMap(
			this::resolve,
			reference -> reference.key().location()
		);

		return ExtraCodecs.overrideLifecycle(
			codec,
			entry -> registry().registrationInfo(entry.key())
				.map(RegistrationInfo::lifecycle)
				.orElseGet(Lifecycle::experimental)
		);

	}

	public Codec<Holder<T>> createEntryCodec(String defaultNamespace) {
		return createReferenceCodec(defaultNamespace).flatComapMap(Function.identity(), entry -> safeCastToReference(registry(), entry));
	}

	public Codec<T> createCodec(String defaultNamespace) {
		return createReferenceCodec(defaultNamespace).flatComapMap(Holder.Reference::value, value -> safeCastToReference(registry(), registry().wrapAsHolder(value)));
	}

	public Codec<Holder.Reference<T>> createReferenceCodec() {
		return createReferenceCodec(ResourceLocation.DEFAULT_NAMESPACE);
	}

	public Codec<Holder<T>> createEntryCodec() {
		return createEntryCodec(ResourceLocation.DEFAULT_NAMESPACE);
	}

	public Codec<T> createCodec() {
		return createCodec(ResourceLocation.DEFAULT_NAMESPACE);
	}

	public static <T, U extends T> RegistryFixedAlias<U> of(Registry<U> registry, RegistryFixedAlias<T> fixed) {
		return new RegistryFixedAlias<>(registry, fixed.aliases);
	}

	public static <T> RegistryFixedAlias<T> of(Registry<T> registry) {
		return new RegistryFixedAlias<>(registry, new ResourceLocationAlias());
	}

	private static <T> DataResult<Holder.Reference<T>> safeCastToReference(Registry<T> registry, Holder<T> value) {
		return value instanceof Holder.Reference<T> reference
			? DataResult.success(reference)
			: DataResult.error(() -> "Unregistered holder in " + registry.key() + ": " + value);
	}

}
