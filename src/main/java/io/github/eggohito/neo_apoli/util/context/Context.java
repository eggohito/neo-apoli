package io.github.eggohito.neo_apoli.util.context;

import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.context.ContextType;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class Context {

	private final ContextParameterMap parameters;

	private final ContextType type;
	private final World world;

	Context(ContextParameterMap parameters, ContextType type, World world) {
		this.parameters = parameters;
		this.type = type;
		this.world = world;
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

	public static Builder builder(ContextType contextType) {
		return new Builder(contextType);
	}

	public static final class Builder {

		private final ContextParameterMap.Builder parameters;
		private final ContextType contextType;

		public Builder(ContextType contextType) {
			this.parameters = new ContextParameterMap.Builder();
			this.contextType = contextType;
		}

		public Builder(Context context) {
			this.parameters = new ContextParameterMap.Builder();
			this.contextType = context.type;
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

		public Context build(World world) {
			return new Context(parameters.build(contextType), contextType, world);
		}

	}

}
