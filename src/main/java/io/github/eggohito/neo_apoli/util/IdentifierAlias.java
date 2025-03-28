package io.github.eggohito.neo_apoli.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public final class IdentifierAlias {

	public static final IdentifierAlias GLOBAL = new IdentifierAlias();

	private final Map<Identifier, Identifier> identifierAliases;
	private final Map<String, String> namespaceAliases;
	private final Map<String, String> pathAliases;

	public IdentifierAlias() {
		this.identifierAliases = new Object2ObjectOpenHashMap<>();
		this.namespaceAliases = new Object2ObjectOpenHashMap<>();
		this.pathAliases = new Object2ObjectOpenHashMap<>();
	}

	public boolean addIdentifierAlias(Identifier from, Identifier to) {
		return identifierAliases.putIfAbsent(from, to) != null;
	}

	public boolean addNamespaceAlias(String from, String to) {
		return namespaceAliases.putIfAbsent(from, to) != null;
	}

	public boolean addPathAlias(String from, String to) {
		return pathAliases.putIfAbsent(from, to) != null;
	}

	public boolean hasIdentifierAlias(Identifier id) {
		return identifierAliases.containsKey(id)
			|| (this != GLOBAL && GLOBAL.hasIdentifierAlias(id));
	}

	public boolean hasNamespaceAlias(String namespace) {
		return namespaceAliases.containsKey(namespace)
			|| (this != GLOBAL && GLOBAL.hasNamespaceAlias(namespace));
	}

	public boolean hasPathAlias(String path) {
		return pathAliases.containsKey(path)
			|| (this != GLOBAL && GLOBAL.hasPathAlias(path));
	}

	public boolean hasAlias(Identifier id) {
		return hasIdentifierAlias(id)
			|| hasNamespaceAlias(id.getNamespace())
			|| hasPathAlias(id.getPath());
	}

	public Identifier resolveIdentifierAlias(Identifier id) {

		if (hasIdentifierAlias(id)) {
			return identifierAliases.get(id);
		}

		else if (this != GLOBAL) {
			return GLOBAL.resolveIdentifierAlias(id);
		}

		else {
			return id;
		}

	}

	public Identifier resolveNamespaceAlias(Identifier id) {

		String namespace = id.getNamespace();
		if (hasNamespaceAlias(namespace)) {
			return Identifier.of(namespaceAliases.get(namespace), id.getPath());
		}

		else if (this != GLOBAL) {
			return GLOBAL.resolveNamespaceAlias(id);
		}

		else {
			return id;
		}

	}

	public Identifier resolvePathAlias(Identifier id) {

		String path = id.getPath();
		if (hasPathAlias(path)) {
			return id.withPath(pathAliases.get(path));
		}

		else if (this != GLOBAL) {
			return GLOBAL.resolvePathAlias(id);
		}

		else {
			return id;
		}

	}

	public Identifier resolve(Identifier id, Predicate<Identifier> until) {

		for (Resolver resolver : Resolver.values()) {

			Identifier aliasedId = resolver.apply(this, id);

			if (until.test(aliasedId)) {
				return aliasedId;
			}

		}

		return id;

	}

	private enum Resolver implements BiFunction<IdentifierAlias, Identifier, Identifier> {

		NO_OP {

			@Override
			public Identifier apply(IdentifierAlias aliases, Identifier id) {
				return id;
			}

		},

		IDENTIFIER {

			@Override
			public Identifier apply(IdentifierAlias aliases, Identifier id) {
				return aliases.resolveIdentifierAlias(id);
			}

		},

		NAMESPACE {

			@Override
			public Identifier apply(IdentifierAlias aliases, Identifier id) {
				return aliases.resolveNamespaceAlias(id);
			}

		},

		PATH {

			@Override
			public Identifier apply(IdentifierAlias aliases, Identifier id) {
				return aliases.resolvePathAlias(id);
			}

		},

		NAMESPACE_AND_PATH {

			@Override
			public Identifier apply(IdentifierAlias aliases, Identifier id) {

				String aliasedNamespace = aliases.resolveNamespaceAlias(id).getNamespace();
				String aliasedPath = aliases.resolvePathAlias(id).getPath();

				return Identifier.of(aliasedNamespace, aliasedPath);

			}

		}

	}

}
