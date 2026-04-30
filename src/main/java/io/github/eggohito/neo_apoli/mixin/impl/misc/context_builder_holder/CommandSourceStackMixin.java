package io.github.eggohito.neo_apoli.mixin.impl.misc.context_builder_holder;

import io.github.eggohito.neo_apoli.api.misc.ContextBuilderHolder;
import io.github.eggohito.neo_apoli.context.Context;
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
