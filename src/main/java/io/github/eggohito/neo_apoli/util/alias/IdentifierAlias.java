package io.github.eggohito.neo_apoli.util.alias;

import io.github.eggohito.neo_apoli.exception.AliasAlreadyTakenException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class IdentifierAlias {

	@Getter(AccessLevel.PROTECTED)
	private final Map<ResourceLocation, ResourceLocation> identifiers;
	@Getter
	private final StringAlias namespaces, paths;

	protected IdentifierAlias(Map<ResourceLocation, ResourceLocation> identifiers, StringAlias namespaces, StringAlias paths) {
		this.identifiers = identifiers;
		this.namespaces = namespaces;
		this.paths = paths;
	}

	public IdentifierAlias() {
		this(new Object2ObjectOpenHashMap<>(), new StringAlias(), new StringAlias());
	}

	public void addAlias(ResourceLocation from, ResourceLocation to) {

		if (getIdentifiers().putIfAbsent(from, to) != null) {
			throw new AliasAlreadyTakenException(from, to, getIdentifiers()::get);
		}

	}

	public boolean hasAlias(ResourceLocation id) {
		return getIdentifiers().containsKey(id);
	}

	public ResourceLocation resolveAlias(ResourceLocation id) {

		if (hasAlias(id)) {
			return getIdentifiers().get(id);
		}

		else {
			return id;
		}

	}

	public ResourceLocation resolve(ResourceLocation id, Predicate<ResourceLocation> until) {

		for (Resolver resolver : Resolver.values()) {

			ResourceLocation aliasedId = resolver.apply(this, id);

			if (until.test(aliasedId)) {
				return aliasedId;
			}

		}

		return id;

	}

	private enum Resolver implements BiFunction<IdentifierAlias, ResourceLocation, ResourceLocation> {

		NO_OP {

			@Override
			public ResourceLocation apply(IdentifierAlias aliases, ResourceLocation id) {
				return id;
			}

		},

		IDENTIFIER {

			@Override
			public ResourceLocation apply(IdentifierAlias aliases, ResourceLocation id) {
				return aliases.resolveAlias(id);
			}

		},

		NAMESPACE {

			@Override
			public ResourceLocation apply(IdentifierAlias aliases, ResourceLocation id) {
				return ResourceLocation.fromNamespaceAndPath(aliases.getNamespaces().resolveAlias(id.getNamespace()), id.getPath());
			}

		},

		PATH {

			@Override
			public ResourceLocation apply(IdentifierAlias aliases, ResourceLocation id) {
				return id.withPath(aliases.getPaths().resolveAlias(id.getPath()));
			}

		},

		NAMESPACE_AND_PATH {

			@Override
			public ResourceLocation apply(IdentifierAlias aliases, ResourceLocation id) {

				String aliasedNamespace = aliases.getNamespaces().resolveAlias(id.getNamespace());
				String aliasedPath = aliases.getPaths().resolveAlias(id.getPath());

				return ResourceLocation.fromNamespaceAndPath(aliasedNamespace, aliasedPath);

			}

		}

	}

}
