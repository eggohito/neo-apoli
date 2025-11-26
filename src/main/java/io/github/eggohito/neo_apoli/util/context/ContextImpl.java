package io.github.eggohito.neo_apoli.util.context;

import net.minecraft.util.context.ContextKeySet;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.level.Level;

import java.util.Set;
import java.util.function.UnaryOperator;

public final class ContextImpl extends AbstractContext<ContextImpl> {

	private ContextImpl(ContextMap.Builder parameters, Set<ContextAware> activeEntries, Level world, ContextAware.ProblemReporter reporter) {
		super(parameters, activeEntries, world, reporter);
	}

	@Override
	protected ContextImpl getThis() {
		return this;
	}

	public static ContextImpl of(Context context, UnaryOperator<io.github.eggohito.neo_apoli.util.context.ContextImpl.Builder> builder) {
		return builder.apply(new io.github.eggohito.neo_apoli.util.context.ContextImpl.Builder(context)).build(context.getWorld());
	}

	public static final class Builder extends Context.Builder<ContextImpl, Level, io.github.eggohito.neo_apoli.util.context.ContextImpl.Builder> {

		public Builder(ContextAware.ProblemReporter reporter) {
			super(reporter);
		}

		public Builder(ContextKeySet type) {
			super(type);
		}

		public Builder(Context context) {
			super(context);
		}

		public Builder() {
			super();
		}

		@Override
		protected io.github.eggohito.neo_apoli.util.context.ContextImpl.Builder getThis() {
			return this;
		}

		@Override
		public ContextImpl build(Level world) {
			return new ContextImpl(this.getParameters(), this.getActiveEntries(), world, this.getReporter());
		}

	}

}
