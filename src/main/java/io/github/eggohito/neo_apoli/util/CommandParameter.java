package io.github.eggohito.neo_apoli.util;

import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

public interface CommandParameter {
	void addAsArgument(CommandBuildContext buildContext, CommandNode<CommandSourceStack> baseNode, CommandNode<CommandSourceStack> parameterNode);
}
