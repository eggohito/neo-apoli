package io.github.eggohito.neo_apoli.util.context;

import com.google.common.collect.ImmutableSet;
import io.github.eggohito.neo_apoli.mixin.access.ContextParameterMapAccessor;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.context.ContextType;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public interface Context extends ContextParameterHolder {

	@Override
	default <T> T required(ContextParameter<T> parameter) {
		return this.getParameters().getOrThrow(parameter);
	}

	@Override
	default <T> @Nullable T nullable(ContextParameter<T> parameter) {
		return this.getParameters().getNullable(parameter);
	}

	ContextAware.ErrorReporter getReporter();

	default ContextType getType() {
		return this.getReporter().getContextType();
	}

	World getWorld();

	ContextParameterMap getParameters();

	ImmutableSet<ContextAware> getActiveEntries();

	Context makeChild(String path);

	Context makeChild(String path, ContextKey key);

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

	abstract class Builder<B extends Builder<B>> implements ContextParameterHolder {

		@Getter(AccessLevel.PROTECTED)
		private final ContextParameterMap.Builder parameters;
		@Getter(AccessLevel.PROTECTED)
		private final Set<ContextAware> activeEntries;

		@Getter
		private ContextAware.ErrorReporter reporter;

		Builder(ContextParameterMap.Builder parameters, Set<ContextAware> activeEntries, ContextAware.ErrorReporter reporter) {
			this.parameters = parameters;
			this.activeEntries = activeEntries;
			this.reporter = reporter;
		}

		public Builder(ContextAware.ErrorReporter reporter) {
			this(new ContextParameterMap.Builder(), new ObjectOpenHashSet<>(), reporter);
		}

		public Builder(ContextType type) {
			this(new ContextAware.ErrorReporter(type));
		}

		public Builder(Context context) {

			ContextParameterMap.Builder newParameters = new ContextParameterMap.Builder();
			((ContextParameterMapAccessor) context.getParameters()).getMap().forEach((parameter, obj) -> ((ContextParameterMapAccessor.BuilderAccessor) newParameters).getMap().put(parameter, obj));

			this.parameters = newParameters;
			this.activeEntries = context.getActiveEntries();
			this.reporter = context.getReporter();

		}

		public Builder() {
			this(LootContextTypes.EMPTY);
		}

		@Override
		public <T> T required(ContextParameter<T> parameter) {
			return this.getParameters().getOrThrow(parameter);
		}

		@Override
		public <T> @Nullable T nullable(ContextParameter<T> parameter) {
			return this.getParameters().getNullable(parameter);
		}

		public <T> B add(ContextParameter<T> parameter, @NotNull T value) {
			this.getParameters().add(parameter, value);
			return getThis();
		}

		public <T> B addIfAbsent(ContextParameter<T> parameter, Supplier<@NotNull T> value) {
			return hasParameter(parameter)
				? getThis()
				: add(parameter, value.get());
		}

		public <T> B addNullable(ContextParameter<T> parameter, @Nullable T value) {
			this.getParameters().addNullable(parameter, value);
			return getThis();
		}

		public <T> B addNullableIfAbsent(ContextParameter<T> parameter, Supplier<@Nullable T> value) {
			return hasParameter(parameter)
				? getThis()
				: addNullable(parameter, value.get());
		}

		public <T> B addOptional(ContextParameter<T> parameter, Optional<T> value) {
			this.addNullable(parameter, value.orElse(null));
			return getThis();
		}

		public <T> B addOptionalIfAbsent(ContextParameter<T> parameter, Supplier<Optional<T>> value) {
			return hasParameter(parameter)
				? getThis()
				: addOptional(parameter, value.get());
		}

		public ContextType getType() {
			return this.getReporter().getContextType();
		}

		public B withContextType(ContextType type) {
			this.reporter = reporter.withContextType(type);
			return getThis();
		}

		public B withReporter(ContextAware.ErrorReporter reporter) {
			this.reporter = reporter;
			return getThis();
		}

		protected abstract B getThis();

	}

}
