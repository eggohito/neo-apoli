package io.github.eggohito.neo_apoli.mixin.misc;

import io.github.eggohito.neo_apoli.duck.ServerContextBuilderHolder;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.SharedSuggestionProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CommandSourceStack.class)
public abstract class CommandSourceStackMixin implements ExecutionCommandSource<CommandSourceStack>, SharedSuggestionProvider, ServerContextBuilderHolder {

	@Unique
	private final ServerContext.Builder neo_apoli$contextBuilder = new ServerContext.Builder();

	@Override
	public ServerContext.Builder neo_apoli$getBuilder() {
		return neo_apoli$contextBuilder;
	}

}
