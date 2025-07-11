package io.github.eggohito.neo_apoli.mixin.access;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.server.command.ExecuteCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ExecuteCommand.class)
public interface ExecuteCommandAccessor {

	@Invoker
	static ArgumentBuilder<ServerCommandSource, ?> callAddConditionLogic(CommandNode<ServerCommandSource> root, ArgumentBuilder<ServerCommandSource, ?> builder, boolean positive, ExecuteCommand.Condition condition) {
		throw new AssertionError();
	}

}
