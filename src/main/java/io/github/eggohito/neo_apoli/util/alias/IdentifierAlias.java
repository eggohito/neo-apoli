package io.github.eggohito.neo_apoli.util.alias;

import io.github.eggohito.neo_apoli.exception.AliasAlreadyTakenException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class IdentifierAlias {

	@Getter(AccessLevel.PROTECTED)
	private final Map<Identifier, Identifier> identifiers;
	@Getter
	private final StringAlias namespaces, paths;

	protected IdentifierAlias(Map<Identifier, Identifier> identifiers, StringAlias namespaces, StringAlias paths) {
		this.identifiers = identifiers;
		this.namespaces = namespaces;
		this.paths = paths;
	}

	public IdentifierAlias() {
		this(new Object2ObjectOpenHashMap<>(), new StringAlias(), new StringAlias());
	}

	public void addAlias(Identifier from, Identifier to) {

		if (getIdentifiers().putIfAbsent(from, to) != null) {
			throw new AliasAlreadyTakenException(from, to, getIdentifiers()::get);
		}

	}

	public boolean hasAlias(Identifier id) {
		return getIdentifiers().containsKey(id);
	}

	public Identifier resolveAlias(Identifier id) {

		if (hasAlias(id)) {
			return getIdentifiers().get(id);
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
				return aliases.resolveAlias(id);
			}

		},

		NAMESPACE {

			@Override
			public Identifier apply(IdentifierAlias aliases, Identifier id) {
				return Identifier.of(aliases.getNamespaces().resolveAlias(id.getNamespace()), id.getPath());
			}

		},

		PATH {

			@Override
			public Identifier apply(IdentifierAlias aliases, Identifier id) {
				return id.withPath(aliases.getPaths().resolveAlias(id.getPath()));
			}

		},

		NAMESPACE_AND_PATH {

			@Override
			public Identifier apply(IdentifierAlias aliases, Identifier id) {

				String aliasedNamespace = aliases.getNamespaces().resolveAlias(id.getNamespace());
				String aliasedPath = aliases.getPaths().resolveAlias(id.getPath());

				return Identifier.of(aliasedNamespace, aliasedPath);

			}

		}

	}

}
