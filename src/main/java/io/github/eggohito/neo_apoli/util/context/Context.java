package io.github.eggohito.neo_apoli.util.context;

import io.github.eggohito.neo_apoli.mixin.access.ContextParameterMapAccessor;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.context.ContextType;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;

public final class Context {

	private final ContextParameterMap parameters;
	private final ContextAware.ErrorReporter reporter;

	private final ContextType type;
	private final World world;

	private final Set<ContextAware> activeContextAwares;

	Context(ContextParameterMap parameters, ContextAware.ErrorReporter reporter, ContextType type, World world) {
		this.parameters = parameters;
		this.reporter = reporter;
		this.type = type;
		this.world = world;
		this.activeContextAwares = new ObjectOpenHashSet<>();
	}

	public Context makeChild(String path) {
		return new Context(this.parameters, this.reporter.makeChild(path), this.type, this.world);
	}

	public Context makeChild(String path, ContextKey key) {
		return new Context(this.parameters, this.reporter.makeChild(path, key), this.type, this.world);
	}

	public boolean isActive(ContextAware contextAware) {
		return activeContextAwares.contains(contextAware);
	}

	public boolean markActive(ContextAware contextAware) {
		return activeContextAwares.add(contextAware);
	}

	public void markInactive(ContextAware contextAware) {
		activeContextAwares.remove(contextAware);
	}

	public ContextAware.ErrorReporter getReporter() {
		return reporter;
	}

	public ContextType getType() {
		return type;
	}

	public World getWorld() {
		return world;
	}

	public <T> T requiredParameter(ContextParameter<T> parameter) {
		return this.parameters.getOrThrow(parameter);
	}

	@Nullable
	public <T> T nullableParameter(ContextParameter<T> parameter) {
		return this.parameters.getNullable(parameter);
	}

	public <T> Optional<T> optionalParameter(ContextParameter<T> parameter) {
		return Optional.ofNullable(this.nullableParameter(parameter));
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

	public Context copy(UnaryOperator<Builder> operator) {
		return operator.apply(builder(this)).build(this.getWorld());
	}

	public static final class Builder {

		private final ContextType contextType;
		private final ContextParameterMap.Builder parameters;

		Builder(ContextType contextType, ContextParameterMap.Builder parameters) {
			this.contextType = contextType;
			this.parameters = parameters;
		}

		public Builder(ContextType contextType) {
			this(contextType, new ContextParameterMap.Builder());
		}

		public Builder(Context context) {

			ContextParameterMap.Builder newParameters = new ContextParameterMap.Builder();
			((ContextParameterMapAccessor) context.parameters).getMap().forEach((parameter, obj) -> ((ContextParameterMapAccessor.BuilderAccessor) newParameters).getMap().put(parameter, obj));

			this.parameters = newParameters;
			this.contextType = context.type;

		}

		public Builder copy() {

			ContextParameterMap.Builder newParameters = new ContextParameterMap.Builder();
			((ContextParameterMapAccessor.BuilderAccessor) this.parameters).getMap().forEach((parameter, obj) -> ((ContextParameterMapAccessor.BuilderAccessor) newParameters).getMap().put(parameter, obj));

			return new Builder(this.contextType, newParameters);

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

		public <T> T get(ContextParameter<T> parameter) {
			return this.parameters.getOrThrow(parameter);
		}

		@Nullable
		public <T> T getNullable(ContextParameter<T> parameter) {
			return this.parameters.getNullable(parameter);
		}

		public <T> Optional<T> getOptional(ContextParameter<T> parameter) {
			return Optional.ofNullable(this.getNullable(parameter));
		}

		public Context build(World world) {

			ContextParameterMap parameters = this.parameters.build(this.contextType);
			ContextAware.ErrorReporter reporter = new ContextAware.ErrorReporter(this.contextType).withWrapperLookup(world.getRegistryManager());

			return new Context(parameters, reporter, this.contextType, world);

		}

	}

}
