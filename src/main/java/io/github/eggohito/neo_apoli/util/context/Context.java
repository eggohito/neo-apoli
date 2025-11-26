package io.github.eggohito.neo_apoli.util.context;

import com.google.common.collect.ImmutableSet;
import io.github.eggohito.neo_apoli.mixin.access.ContextMapAccessor;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public interface Context extends ContextParameterHolder {

	@Override
	default <T> T required(ContextKey<T> parameter) {
		return this.getParameters().getOrThrow(parameter);
	}

	@Override
	default <T> @Nullable T nullable(ContextKey<T> parameter) {
		return this.getParameters().getOptional(parameter);
	}

	ContextAware.ProblemReporter getReporter();

	default ContextKeySet getKeySet() {
		return this.getReporter().getKeySet();
	}

	Level getWorld();

	ContextMap getParameters();

	ImmutableSet<ContextAware> getActiveEntries();

	Context makeChild(String path);

	Context makeChild(String path, ReferenceKey key);

	default boolean isActive(ContextAware entry) {
		return this.getActiveEntries().contains(entry);
	}

	boolean markActive(ContextAware entry);

	boolean markInActive(ContextAware entry);

	default boolean hasErrors() {
		return this.getReporter().hasErrors();
	}

	default boolean hasAnyErrors() {
		return this.getReporter().hasAnyErrors();
	}

	abstract class Builder<C extends Context, W extends Level, B extends Builder<C, W, B>> implements ContextParameterHolder {

		@Getter(AccessLevel.PROTECTED)
		private final ContextMap.Builder parameters;
		@Getter(AccessLevel.PROTECTED)
		private final Set<ContextAware> activeEntries;

		@Getter
		private ContextAware.ProblemReporter reporter;

		Builder(ContextMap.Builder parameters, Set<ContextAware> activeEntries, ContextAware.ProblemReporter reporter) {
			this.parameters = parameters;
			this.activeEntries = activeEntries;
			this.reporter = reporter;
		}

		public Builder(ContextAware.ProblemReporter reporter) {
			this(new ContextMap.Builder(), new ObjectOpenHashSet<>(), reporter);
		}

		public Builder(ContextKeySet type) {
			this(new ContextAware.ProblemReporter(type));
		}

		public Builder(Context context) {

			ContextMap.Builder newParameters = new ContextMap.Builder();
			((ContextMapAccessor) context.getParameters()).getParams().forEach((parameter, obj) -> ((ContextMapAccessor.BuilderAccessor) newParameters).getParams().put(parameter, obj));

			this.parameters = newParameters;
			this.activeEntries = context.getActiveEntries();
			this.reporter = context.getReporter();

		}

		public Builder() {
			this(LootContextParamSets.EMPTY);
		}

		@Override
		public <T> T required(ContextKey<T> parameter) {
			return this.getParameters().getParameter(parameter);
		}

		@Override
		public <T> @Nullable T nullable(ContextKey<T> parameter) {
			return this.getParameters().getOptionalParameter(parameter);
		}

		public <T> B add(ContextKey<T> parameter, @NotNull T value) {
			this.getParameters().withParameter(parameter, value);
			return getThis();
		}

		public <T> B addIfAbsent(ContextKey<T> parameter, Supplier<@NotNull T> value) {
			return hasParameter(parameter)
				? getThis()
				: add(parameter, value.get());
		}

		public <T> B addNullable(ContextKey<T> parameter, @Nullable T value) {
			this.getParameters().withOptionalParameter(parameter, value);
			return getThis();
		}

		public <T> B addNullableIfAbsent(ContextKey<T> parameter, Supplier<@Nullable T> value) {
			return hasParameter(parameter)
				? getThis()
				: addNullable(parameter, value.get());
		}

		public <T> B addOptional(ContextKey<T> parameter, Optional<T> value) {
			this.addNullable(parameter, value.orElse(null));
			return getThis();
		}

		public <T> B addOptionalIfAbsent(ContextKey<T> parameter, Supplier<Optional<T>> value) {
			return hasParameter(parameter)
				? getThis()
				: addOptional(parameter, value.get());
		}

		public ContextKeySet getKeySet() {
			return this.getReporter().getKeySet();
		}

		public B withKeySet(ContextKeySet keySet) {
			this.reporter = reporter.withKeySet(keySet);
			return getThis();
		}

		public B withReporter(ContextAware.ProblemReporter reporter) {
			this.reporter = reporter;
			return getThis();
		}

		public boolean isActive(ContextAware entry) {
			return this.getActiveEntries().contains(entry);
		}

		protected abstract B getThis();

		public abstract C build(W world);

	}

}
