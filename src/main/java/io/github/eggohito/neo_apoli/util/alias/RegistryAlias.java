package io.github.eggohito.neo_apoli.util.alias;

import io.github.eggohito.neo_apoli.exception.AliasAlreadyTakenException;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import lombok.Getter;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

@Getter
public class RegistryAlias<T> extends IdentifierAlias {

	private final Registry<T> registry;

	public RegistryAlias(Registry<T> registry) {
		this.registry = registry;
	}

	public boolean hasNamespaceAlias(T t) {
		return this.getNamespaces().hasAlias(RegistryUtil.getIdNamespace(registry, t));
	}

	public boolean hasPathAlias(T t) {
		return this.getPaths().hasAlias(RegistryUtil.getIdPath(registry, t));
	}

	public boolean hasAlias(T t) {
		return this.hasAlias(RegistryUtil.getId(registry, t));
	}

	public void addNamespaceAlias(String from, T to) {

		StringAlias namespaceAliases = this.getNamespaces();
		String toNamespace = RegistryUtil.getIdNamespace(registry, to);

		try  {
			namespaceAliases.addAlias(from, toNamespace);
		}

		catch (AliasAlreadyTakenException e) {
			throw new AliasAlreadyTakenException(e.getMessage() + " in registry " + registry.getKey());
		}

	}

	public void addPathAlias(String from, T to) {

		StringAlias pathAliases = this.getPaths();
		String toNamespace = RegistryUtil.getIdNamespace(registry, to);

		try  {
			pathAliases.addAlias(from, toNamespace);
		}

		catch (AliasAlreadyTakenException e) {
			throw new AliasAlreadyTakenException(e.getMessage() + " in registry " + registry.getKey());
		}

	}

	public void addAlias(Identifier from, T to) {

		Identifier toId = RegistryUtil.getId(registry, to);
		try {
			this.addAlias(from, toId);
		}

		catch (AliasAlreadyTakenException e) {
			throw new AliasAlreadyTakenException(e.getMessage() + " in registry " + registry.getKey());
		}

	}

}
