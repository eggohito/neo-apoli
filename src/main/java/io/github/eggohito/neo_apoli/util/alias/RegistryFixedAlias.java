package io.github.eggohito.neo_apoli.util.alias;

import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.exception.AliasAlreadyTakenException;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class RegistryFixedAlias<T> {

	@Getter
	private final Registry<T> registry;
	private final IdentifierAlias aliases;

	private final String pathPrefix;
	private final String pathSuffix;

	public boolean hasNamespaceAlias(T t) {
		return aliases.getNamespaces().hasAlias(RegistryUtil.getIdNamespace(getRegistry(), t));
	}

	public boolean hasPathAlias(T t) {
		return aliases.getPaths().hasAlias(RegistryUtil.getIdPath(getRegistry(), t));
	}

	public boolean hasIdAlias(T t) {
		return aliases.hasAlias(RegistryUtil.getId(getRegistry(), t));
	}

	public void addNamespaceAlias(String from, T to) {

		try {
			aliases.getNamespaces().addAlias(from, RegistryUtil.getIdNamespace(getRegistry(), to));
		}

		catch (AliasAlreadyTakenException e) {
			throw new AliasAlreadyTakenException(e, getRegistry().key());
		}

	}

	public void addPathAlias(String from, T to) {

		try {
			aliases.getPaths().addAlias(pathPrefix + from + pathSuffix, RegistryUtil.getIdPath(getRegistry(), to));
		}

		catch (AliasAlreadyTakenException e) {
			throw new AliasAlreadyTakenException(e, getRegistry().key());
		}

	}

	public void addIdAlias(ResourceLocation from, T to) {

		try {
			aliases.addAlias(from, RegistryUtil.getId(getRegistry(), to));
		}

		catch (AliasAlreadyTakenException e) {
			throw new AliasAlreadyTakenException(e, getRegistry().key());
		}

	}

	public DataResult<Holder.Reference<T>> resolve(ResourceLocation id) {
		return registry.get(aliases.resolve(id, registry::containsKey))
			.map(DataResult::success)
			.orElseGet(() -> DataResult.error(() -> "Unknown registry key in " + registry.key() + ": " + id));
	}

	public static <T, U extends T> RegistryFixedAlias<U> of(Registry<U> registry, RegistryFixedAlias<T> fixed, String pathPrefix, String pathSuffix) {
		return new RegistryFixedAlias<>(registry, fixed.aliases, pathPrefix, pathSuffix);
	}

	public static <T, U extends T> RegistryFixedAlias<U> of(Registry<U> registry, RegistryFixedAlias<T> fixed) {
		return of(registry, fixed, "", "");
	}

	public static <T> RegistryFixedAlias<T> of(Registry<T> registry) {
		return new RegistryFixedAlias<>(registry, new IdentifierAlias(), "", "");
	}

}
