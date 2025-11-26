package io.github.eggohito.neo_apoli.mixin.access;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.ExecuteCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ExecuteCommand.class)
public interface ExecuteCommandAccessor {

	@Invoker
	static ArgumentBuilder<CommandSourceStack, ?> callAddConditional(CommandNode<CommandSourceStack> root, ArgumentBuilder<CommandSourceStack, ?> builder, boolean positive, ExecuteCommand.CommandPredicate condition) {
		throw new AssertionError();
	}

}
