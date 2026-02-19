package io.github.eggohito.neo_apoli.context;

import com.google.common.collect.Sets;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.context.parameter.ContextParameter;
import io.github.eggohito.neo_apoli.context.visitor.ClearableVisitor;
import io.github.eggohito.neo_apoli.context.visitor.Visitor;
import io.github.eggohito.neo_apoli.util.Reporter;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public final class Context implements ContextParamsHolder {

	public static final ClearableVisitor<ContextUser> GLOBAL_VISITOR = ClearableVisitor.createThreadLocalized();

	@Getter
	private final Level level;
	@Getter
	private final Reporter reporter;

	private final ContextParams params;
	private final Set<ContextUser> visited;

	private Context(Level level, Reporter reporter, ContextParams params, Set<ContextUser> visited) {
		this.level = level;
		this.reporter = reporter;
		this.params = params;
		this.visited = visited;
	}

	@Override
	public @Nullable <T> T getNullable(ContextKey<T> parameter) {
		return params.getNullable(parameter);
	}

	public Visitor<ContextUser> visitor() {
		return new Visitor<>() {

			@Override
			public boolean contains(ContextUser element) {
				return visited.contains(element);
			}

			@Override
			public boolean push(ContextUser element) {
				return visited.add(element);
			}

			@Override
			public void pop(ContextUser element) {
				visited.remove(element);
			}

		};
	}

	public ContextKeySet toKeySet() {
		return params.toKeySet();
	}

	public Context forChild(String path) {
		return new Context(this.level(), this.reporter().forChild(path), this.params, this.visited);
	}

	public void reportProblem(String message) {
		this.reporter.report(message);
	}

	public boolean hasErrors() {
		return reporter().hasErrors();
	}

	public boolean hasAnyErrors() {
		return reporter().hasAnyErrors();
	}

	public static class Builder implements ContextParamsHolder {

		private final ContextParams.Builder params;
		private final Set<ContextUser> visited;
		private Reporter reporter;

		Builder(ContextParams.Builder params, Reporter reporter, Set<ContextUser> visited) {
			this.params = params;
			this.reporter = reporter;
			this.visited = visited;
		}

		public Builder(Context context) {
			this(context.params.toBuilder(), context.reporter, context.visited);
		}

		public Builder() {
			this(new ContextParams.Builder(), new Reporter(), new ObjectOpenHashSet<>());
		}

		@Override
		public @Nullable <T> T getNullable(ContextKey<T> parameter) {
			return params.getNullable(parameter);
		}

		public <T> Builder withNullable(ContextParameter<T> key, @Nullable T value) {
			this.params.withNullable(key,value);
			return this;
		}

		public <T> Builder withNullableIfAbsent(ContextParameter<T> key, Supplier<@Nullable T> value) {
			this.params.withNullableIfAbsent(key, value);
			return this;
		}

		public <T> Builder withRequired(ContextParameter<T> key, @NotNull T value) {
			this.params.withRequired(key, value);
			return this;
		}

		public <T> Builder withRequiredIfAbsent(ContextParameter<T> key, Supplier<@NotNull T> value) {
			this.params.withRequiredIfAbsent(key, value);
			return this;
		}

		public <T> Builder withOptional(ContextParameter<T> key, Optional<T> value) {
			return this.withNullable(key, value.orElse(null));
		}

		public <T> Builder withOptionalIfAbsent(ContextParameter<T> key, Supplier<Optional<T>> value) {
			this.params.withOptionalIfAbsent(key, value);
			return this;
		}

		public Builder withReporter(Reporter reporter) {
			this.reporter = reporter;
			return this;
		}

		public ContextKeySet toKeySet() {
			return params.toKeySet();
		}

		public Context buildWithRequirements(Level level, ContextKeySet keySet) {
			return new Context(level, reporter, params.buildWithRequirements(keySet), visited);
		}

		public Context build(Level level) {
			return new Context(level, reporter, params.build(), visited);
		}

	}

	public static class Validator {

		@Getter
		private final ContextKeySet keySet;
		@Getter
		private final Reporter reporter;

		private final Optional<HolderLookup.Provider> resolver;
		private final Set<ResourceKey<?>> visited;

		Validator(ContextKeySet keySet, Reporter reporter, Optional<HolderLookup.Provider> resolver, Set<ResourceKey<?>> visited) {
			this.keySet = keySet;
			this.reporter = reporter;
			this.resolver = resolver;
			this.visited = visited;
		}

		public Validator(ContextKeySet keySet, Reporter reporter, @NotNull HolderLookup.Provider resolver) {
			this(keySet, reporter, Optional.of(resolver), new ObjectOpenHashSet<>());
		}

		public Validator(ContextKeySet keySet, Reporter reporter) {
			this(keySet, reporter, Optional.empty(), new ObjectOpenHashSet<>());
		}

		public Validator withKeySet(ContextKeySet keySet) {
			return new Validator(keySet, reporter(), this.resolver, this.visited);
		}

		public Validator withAdditionalKeysFromSets(ContextKeySet... keySets) {
			ContextKeySet[] merged = ArrayUtils.add(keySets, this.keySet());
			return new Validator(ContextHelper.mergeKeySets(merged), reporter(), this.resolver, this.visited);
		}

		public Validator withResolver(@NotNull HolderLookup.Provider resolver) {
			return new Validator(this.keySet, reporter, Optional.of(resolver), this.visited);
		}

		public Validator forChild(String path) {
			return new Validator(keySet(), reporter().forChild(path), this.resolver, this.visited);
		}

		public Validator visitChild(String path, ResourceKey<?> key) {
			this.visited.add(key);
			return new Validator(keySet(), reporter().forChild(path), this.resolver, this.visited);
		}

		public HolderLookup.Provider resolver() {
			return resolver.orElseThrow(() -> new UnsupportedOperationException("References are not allowed!"));
		}

		public Visitor<ResourceKey<?>> visitor() {
			return new Visitor<>() {

				@Override
				public boolean contains(ResourceKey<?> element) {
					return visited.contains(element);
				}

				@Override
				public boolean push(ResourceKey<?> element) {
					return visited.add(element);
				}

				@Override
				public void pop(ResourceKey<?> element) {
					visited.remove(element);
				}

			};
		}

		public boolean allowsReferences() {
			return resolver.isPresent();
		}

		public boolean hasVisited(ResourceKey<?> key) {
			return visited.contains(key);
		}

		public void reportProblem(String message) {
			this.reporter.report(message);
		}

		public void validate(ContextUser user) {

			Set<ContextKey<?>> missing = Sets.difference(user.getRequiredParameters(), keySet().allowed());

			if (!missing.isEmpty()) {
				this.reportProblem("The following parameters are not provided: " + missing);
			}

		}

	}

	public static <T> ContextParameter<T> parameter(ResourceLocation name, Class<T> typeClass) {
		return new ContextParameter<>(name) {

			@Override
			public @NotNull Class<T> typeClass() {
				return typeClass;
			}

			@Override
			public void addAsArgument(CommandBuildContext buildContext, CommandNode<CommandSourceStack> baseNode, CommandNode<CommandSourceStack> parameterNode) {
				// No-op; extend the Context$Key class to implement adding the key as a command argument
			}

		};
	}

}
