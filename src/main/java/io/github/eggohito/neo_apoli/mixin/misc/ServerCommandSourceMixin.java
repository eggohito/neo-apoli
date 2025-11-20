package io.github.eggohito.neo_apoli.mixin.misc;

import io.github.eggohito.neo_apoli.duck.ServerContextBuilderHolder;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerCommandSource.class)
public abstract class ServerCommandSourceMixin implements AbstractServerCommandSource<ServerCommandSource>, CommandSource, ServerContextBuilderHolder {

	@Unique
	private final ServerContext.Builder neo_apoli$contextBuilder = new ServerContext.Builder();

	@Override
	public ServerContext.Builder neo_apoli$getBuilder() {
		return neo_apoli$contextBuilder;
	}

}
