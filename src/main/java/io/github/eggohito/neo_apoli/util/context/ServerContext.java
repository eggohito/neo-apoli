package io.github.eggohito.neo_apoli.util.context;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.context.ContextType;

import java.util.function.UnaryOperator;

public class ServerContext extends Context {

	ServerContext(ContextParameterMap parameters, ContextAware.ErrorReporter reporter, ContextType type, ServerWorld world) {
		super(parameters, reporter, type, world);
	}

	public ServerContext(Context context, ServerWorld serverWorld) {
		this(context.parameters, context.getReporter(), context.getType(), serverWorld);
	}

	@Override
	public ServerContext makeChild(String path) {
		return new ServerContext(this.parameters, this.reporter.makeChild(path), this.type, this.getWorld());
	}

	@Override
	public ServerContext makeChild(String path, ContextKey key) {
		return new ServerContext(this.parameters, this.reporter.makeChild(path, key), this.type, this.getWorld());
	}

	@Override
	public ServerWorld getWorld() {
		return (ServerWorld) super.getWorld();
	}

	@Override
	public ServerContext copy(UnaryOperator<Builder> operator) {
		Context context = operator.apply(builder(this)).build(this.getWorld());
		return new ServerContext(context, this.getWorld());
	}

	public MinecraftServer getServer() {
		return this.getWorld().getServer();
	}

}
