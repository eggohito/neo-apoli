package io.github.eggohito.neo_apoli.util.context;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.context.ContextType;

import java.util.Set;

public final class ServerContext extends AbstractContext<ServerContext> {

	private ServerContext(ContextParameterMap.Builder parameters, Set<ContextAware> activeEntries, ServerWorld world, ContextAware.ErrorReporter reporter) {
		super(parameters, activeEntries, world, reporter);
	}

	@Override
	public ServerWorld getWorld() {
		return (ServerWorld) super.getWorld();
	}

	@Override
	protected ServerContext getThis() {
		return this;
	}

	public MinecraftServer getServer() {
		return this.getWorld().getServer();
	}

	public static final class Builder extends AbstractContext.Builder<ServerContext, ServerWorld, Builder> {

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
		public ServerContext build(ServerWorld world) {
			return new ServerContext(this.getParameters(), this.getActiveEntries(), world, this.getReporter());
		}

	}

}
