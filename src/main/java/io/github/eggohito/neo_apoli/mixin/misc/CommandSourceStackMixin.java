package io.github.eggohito.neo_apoli.mixin.misc;

import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextBuilderHolder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.SharedSuggestionProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CommandSourceStack.class)
public abstract class CommandSourceStackMixin implements ExecutionCommandSource<CommandSourceStack>, SharedSuggestionProvider, ContextBuilderHolder {

	@Unique
	private final Context.Builder neo_apoli$contextBuilder = new Context.Builder();

	@Override
	public Context.Builder neo_apoli$getContextBuilder() {
		return neo_apoli$contextBuilder;
	}

}
