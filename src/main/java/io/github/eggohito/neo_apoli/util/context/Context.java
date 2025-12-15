package io.github.eggohito.neo_apoli.util.context;

import com.google.common.collect.ImmutableSet;
import io.github.eggohito.neo_apoli.mixin.access.ContextMapAccessor;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Context implements ContextParameterHolder {

	protected final Level level;
	protected final ContextAware.ProblemReporter reporter;

	protected ContextMap parameters;
	protected ImmutableSet<ContextAware> activeEntries;

	@Override
	public <T> T required(ContextKey<T> parameter) {
		return this.getParameters().getOrThrow(parameter);
	}

	@Override
	public <T> @Nullable T nullable(ContextKey<T> parameter) {
		return this.getParameters().getOptional(parameter);
	}

	public ContextKeySet getKeySet() {
		return getReporter().getKeySet();
	}

	public Context forChild(String path) {
		return new Context(this.level, this.reporter.forChild(path), this.parameters, this.activeEntries);
	}

	public Context forChild(String path, ReferenceKey key) {
		return new Context(this.level, this.reporter.forChild(path, key), this.parameters, this.activeEntries);
	}

	public boolean isActive(ContextAware entry) {
		return this.getActiveEntries().contains(entry);
	}

	public boolean markActive(ContextAware entry) {

		Set<ContextAware> newEntries = new ObjectOpenHashSet<>(this.activeEntries);
		boolean added = newEntries.add(entry);

		this.activeEntries = ImmutableSet.copyOf(newEntries);
		return added;

	}

	public boolean markInActive(ContextAware entry) {

		Set<ContextAware> newEntries = new ObjectOpenHashSet<>(this.activeEntries);
		boolean removed = newEntries.remove(entry);

		this.activeEntries = ImmutableSet.copyOf(newEntries);
		return removed;

	}

	public boolean hasErrors() {
		return this.getReporter().hasErrors();
	}

	public boolean hasAnyErrors() {
		return this.getReporter().hasAnyErrors();
	}

	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class Builder implements ContextParameterHolder {

		private final ContextMap.Builder parameters;
		private ImmutableSet<ContextAware> activeEntries;

		@Getter
		private ContextAware.ProblemReporter reporter;

		public Builder(ContextAware.ProblemReporter reporter) {
			this(new ContextMap.Builder(), ImmutableSet.of(), reporter);
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
			return this.parameters.getParameter(parameter);
		}

		@Override
		public <T> @Nullable T nullable(ContextKey<T> parameter) {
			return this.parameters.getOptionalParameter(parameter);
		}

		public <T> Builder add(ContextKey<T> parameter, @NotNull T value) {
			this.parameters.withParameter(parameter, value);
			return this;
		}

		public <T> Builder addIfAbsent(ContextKey<T> parameter, Supplier<@NotNull T> value) {
			return hasParameter(parameter)
				? this
				: add(parameter, value.get());
		}

		public <T> Builder addNullable(ContextKey<T> parameter, @Nullable T value) {
			this.parameters.withOptionalParameter(parameter, value);
			return this;
		}

		public <T> Builder addNullableIfAbsent(ContextKey<T> parameter, Supplier<@Nullable T> value) {
			return hasParameter(parameter)
				? this
				: addNullable(parameter, value.get());
		}

		public <T> Builder addOptional(ContextKey<T> parameter, Optional<T> value) {
			this.addNullable(parameter, value.orElse(null));
			return this;
		}

		public <T> Builder addOptionalIfAbsent(ContextKey<T> parameter, Supplier<Optional<T>> value) {
			return hasParameter(parameter)
				? this
				: addOptional(parameter, value.get());
		}

		public Builder withKeySet(ContextKeySet keySet) {
			this.reporter = reporter.withKeySet(keySet);
			return this;
		}

		public Builder withReporter(ContextAware.ProblemReporter reporter) {
			this.reporter = reporter;
			return this;
		}

		public Context build(Level level) {
			return new Context(level, this.getReporter(), this.parameters.create(this.getKeySet()), this.activeEntries);
		}

		public ContextKeySet getKeySet() {
			return this.getReporter().getKeySet();
		}

		public boolean isActive(ContextAware entry) {
			return this.activeEntries.contains(entry);
		}

	}

}
