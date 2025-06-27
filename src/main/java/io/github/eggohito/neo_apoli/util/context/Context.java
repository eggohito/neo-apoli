package io.github.eggohito.neo_apoli.util.context;

import io.github.eggohito.neo_apoli.mixin.access.ContextParameterMapAccessor;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
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
import java.util.function.UnaryOperator;

public class Context {

	protected final ContextParameterMap parameters;
	@Getter
	protected final ContextAware.ErrorReporter reporter;

	@Getter
	protected final ContextType type;
	@Getter
	protected final World world;

	protected final Set<ContextAware> activeEntries;

	Context(ContextParameterMap parameters, ContextAware.ErrorReporter reporter, ContextType type, World world) {
		this.parameters = parameters;
		this.reporter = reporter;
		this.type = type;
		this.world = world;
		this.activeEntries = new ObjectOpenHashSet<>();
	}

	public Context makeChild(String path) {
		return new Context(this.parameters, this.reporter.makeChild(path), this.type, this.world);
	}

	public Context makeChild(String path, ContextKey key) {
		return new Context(this.parameters, this.reporter.makeChild(path, key), this.type, this.world);
	}

	public boolean isActive(ContextAware contextAware) {
		return activeEntries.contains(contextAware);
	}

	public boolean markActive(ContextAware contextAware) {
		return activeEntries.add(contextAware);
	}

	public void markInactive(ContextAware contextAware) {
		activeEntries.remove(contextAware);
	}

	public <T> T required(ContextParameter<T> parameter) {
		return this.parameters.getOrThrow(parameter);
	}

	@Nullable
	public <T> T nullable(ContextParameter<T> parameter) {
		return this.parameters.getNullable(parameter);
	}

	public <T> Optional<T> optional(ContextParameter<T> parameter) {
		return Optional.ofNullable(this.nullable(parameter));
	}

	public boolean hasParameter(ContextParameter<?> parameter) {
		return this.parameters.contains(parameter);
	}

	public boolean hasErrors() {
		return reporter.hasErrors();
	}

	public boolean hasAnyErrors() {
		return reporter.hasAnyErrors();
	}

	public static Builder builder(ContextType contextType) {
		return new Builder(contextType);
	}

	public static Builder builder(Context context) {
		return new Builder(context);
	}

	public static Builder builder() {
		return builder(LootContextTypes.EMPTY);
	}

	public Context copy(UnaryOperator<Builder> operator) {
		return operator.apply(builder(this)).build(this.getWorld());
	}

	public static class Builder {

		private ContextType contextType;
		private ContextAware.ErrorReporter reporter;

		private final ContextParameterMap.Builder parameters;

		Builder(ContextType contextType, ContextParameterMap.Builder parameters, ContextAware.ErrorReporter reporter) {
			this.contextType = contextType;
			this.parameters = parameters;
			this.reporter = reporter;
		}

		public Builder(ContextType contextType) {
			this(contextType, new ContextParameterMap.Builder(), new ContextAware.ErrorReporter(contextType));
		}

		public Builder(Context context) {

			ContextParameterMap.Builder newParameters = new ContextParameterMap.Builder();
			((ContextParameterMapAccessor) context.parameters).getMap().forEach((parameter, obj) -> ((ContextParameterMapAccessor.BuilderAccessor) newParameters).getMap().put(parameter, obj));

			this.parameters = newParameters;
			this.contextType = context.getType();
			this.reporter = context.getReporter();

		}

		public Builder withContextType(@NotNull ContextType contextType) {
			this.contextType = contextType;
			return this;
		}

		public Builder withReporter(@NotNull ContextAware.ErrorReporter reporter) {

			this.contextType = reporter.getContextType();
			this.reporter = reporter;

			return this;

		}

		public Builder copy() {

			ContextParameterMap.Builder newParameters = new ContextParameterMap.Builder();
			((ContextParameterMapAccessor.BuilderAccessor) this.parameters).getMap().forEach((parameter, obj) -> ((ContextParameterMapAccessor.BuilderAccessor) newParameters).getMap().put(parameter, obj));

			return new Builder(this.contextType, newParameters, this.reporter);

		}

		public <T> Builder add(ContextParameter<T> parameter, @NotNull T value) {
			this.parameters.add(parameter, value);
			return this;
		}

		public <T> Builder addNullable(ContextParameter<T> parameter, @Nullable T value) {
			this.parameters.addNullable(parameter, value);
			return this;
		}

		public <T> Builder addOptional(ContextParameter<T> parameter, Optional<T> value) {
			return addNullable(parameter, value.orElse(null));
		}

		public <T> T required(ContextParameter<T> parameter) {
			return this.parameters.getOrThrow(parameter);
		}

		@Nullable
		public <T> T nullable(ContextParameter<T> parameter) {
			return this.parameters.getNullable(parameter);
		}

		public <T> Optional<T> optional(ContextParameter<T> parameter) {
			return Optional.ofNullable(this.nullable(parameter));
		}

		public Context build(World world) {
			return new Context(this.parameters.build(this.contextType), this.reporter.withWrapperLookup(world.getRegistryManager()), this.contextType, world);
		}

	}

}
