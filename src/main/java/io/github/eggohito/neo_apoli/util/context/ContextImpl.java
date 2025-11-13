package io.github.eggohito.neo_apoli.util.context;

import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.context.ContextType;
import net.minecraft.world.World;

import java.util.Set;
import java.util.function.UnaryOperator;

public final class ContextImpl extends AbstractContext<ContextImpl> {

	private ContextImpl(ContextParameterMap.Builder parameters, Set<ContextAware> activeEntries, World world, ContextAware.ErrorReporter reporter) {
		super(parameters, activeEntries, world, reporter);
	}

	@Override
	protected ContextImpl getThis() {
		return this;
	}

	public static ContextImpl of(Context context, UnaryOperator<Builder> builder) {
		return builder.apply(new Builder(context)).build(context.getWorld());
	}

	public static final class Builder extends Context.Builder<ContextImpl, World, Builder> {

		public Builder(ContextAware.ErrorReporter reporter) {
			super(reporter);
		}

		public Builder(ContextType type) {
			super(type);
		}

		public Builder(Context context) {
			super(context);
		}

		public Builder() {
			super();
		}

		@Override
		protected Builder getThis() {
			return this;
		}

		@Override
		public ContextImpl build(World world) {
			return new ContextImpl(this.getParameters(), this.getActiveEntries(), world, this.getReporter());
		}

	}

}
