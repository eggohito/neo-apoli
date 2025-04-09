package io.github.eggohito.neo_apoli.provider.context;

import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.context.ContextType;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ValueProviderContext {

	private final ContextParameterMap parameters;
	private final World world;

	ValueProviderContext(ContextParameterMap parameters, World world) {
		this.parameters = parameters;
		this.world = world;
	}

	public World getWorld() {
		return world;
	}

	public <T> T requireParameter(ContextParameter<T> parameter) {
		return this.parameters.getOrThrow(parameter);
	}

	@Nullable
	public <T> T parameter(ContextParameter<T> parameter) {
		return this.parameters.getNullable(parameter);
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

		public <T> Builder add(ContextParameter<T> parameter, @NotNull T value) {
			this.parameters.add(parameter, value);
			return this;
		}

		public <T> Builder addOptional(ContextParameter<T> parameter, @Nullable T value) {
			this.parameters.addNullable(parameter, value);
			return this;
		}

		public <T> T get(ContextParameter<T> parameter) {
			return this.parameters.getOrThrow(parameter);
		}

		@Nullable
		public <T> T getOptional(ContextParameter<T> parameter) {
			return this.parameters.getNullable(parameter);
		}

		public ValueProviderContext build(World world) {
			ContextParameterMap parameters = this.parameters.build(this.contextType);
			return new ValueProviderContext(parameters, world);
		}

	}

}
