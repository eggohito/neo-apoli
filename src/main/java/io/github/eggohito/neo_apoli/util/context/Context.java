package io.github.eggohito.neo_apoli.util.context;

import io.github.eggohito.neo_apoli.mixin.access.ContextParameterMapAccessor;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.context.ContextType;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.UnaryOperator;

public final class Context {

	private final ContextParameterMap parameters;
	private final ContextAware.ErrorReporter reporter;

	private final ContextType type;
	private final World world;

	Context(ContextParameterMap parameters, ContextAware.ErrorReporter reporter, ContextType type, World world) {
		this.parameters = parameters;
		this.reporter = reporter;
		this.type = type;
		this.world = world;
	}

	public Context makeChild(String path) {
		return new Context(this.parameters, this.reporter.makeChild(path), this.type, this.world);
	}

	public Context makeChild(String path, ContextKey key) {
		return new Context(this.parameters, this.reporter.makeChild(path, key), this.type, this.world);
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

	public boolean pathHasErrors() {
		return reporter.pathHasErrors();
	}

	public boolean hasErrors() {
		return reporter.hasErrors();
	}

	public static Builder builder(ContextType contextType) {
		return new Builder(contextType);
	}

	public static Builder builder(Context context) {
		return new Builder(context);
	}

	public static Context copy(Context context, UnaryOperator<Builder> operator) {
		return operator.apply(builder(context)).build(context.getWorld());
	}

	public static final class Builder {

		private final ContextParameterMap.Builder parameters;
		private final ContextType contextType;

		private ContextAware.ErrorReporter reporter;

		public Builder(ContextType contextType) {
			this.parameters = new ContextParameterMap.Builder();
			this.contextType = contextType;
			this.reporter = new ContextAware.ErrorReporter(contextType);
		}

		public Builder(Context context) {

			ContextParameterMap.Builder builder = new ContextParameterMap.Builder();
			((ContextParameterMapAccessor) context.parameters).getMap().forEach((parameter, obj) -> ((ContextParameterMapAccessor.BuilderAccessor) builder).getMap().put(parameter, obj));

			this.parameters = builder;
			this.contextType = context.type;

			this.reporter = context.reporter;

		}

		public <T> Builder add(ContextParameter<T> parameter, @NotNull T value) {
			this.parameters.add(parameter, value);
			return this;
		}

		public <T> Builder addNullable(ContextParameter<T> parameter, @Nullable T value) {
			this.parameters.addNullable(parameter, value);
			return this;
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

		public ContextAware.ErrorReporter getReporter() {
			return reporter;
		}

		public Builder withReporter(UnaryOperator<ContextAware.ErrorReporter> operator) {
			this.reporter = operator.apply(this.reporter);
			return this;
		}

		public Context build(World world) {
			return new Context(parameters.build(contextType), reporter, contextType, world);
		}

	}

}
