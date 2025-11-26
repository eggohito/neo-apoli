package io.github.eggohito.neo_apoli.util.context;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.util.context.ContextMap;

import java.util.Set;

public final class ServerContext extends AbstractContext<ServerContext> {

	private ServerContext(ContextMap.Builder parameters, Set<ContextAware> activeEntries, ServerLevel world, ContextAware.ProblemReporter reporter) {
		super(parameters, activeEntries, world, reporter);
	}

	@Override
	public ServerLevel getWorld() {
		return (ServerLevel) super.getWorld();
	}

	@Override
	protected ServerContext getThis() {
		return this;
	}

	public MinecraftServer getServer() {
		return this.getWorld().getServer();
	}

	public static final class Builder extends Context.Builder<ServerContext, ServerLevel, io.github.eggohito.neo_apoli.util.context.ServerContext.Builder> {

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
		protected io.github.eggohito.neo_apoli.util.context.ServerContext.Builder getThis() {
			return this;
		}

		@Override
		public ServerContext build(ServerLevel world) {
			return new ServerContext(this.getParameters(), this.getActiveEntries(), world, this.getReporter());
		}

	}

}
