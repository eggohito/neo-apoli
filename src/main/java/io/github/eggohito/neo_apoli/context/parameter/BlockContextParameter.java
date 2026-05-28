package io.github.eggohito.neo_apoli.context.parameter;


import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.util.CachedBlock;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Predicate;

public final class BlockContextParameter extends Context.Parameter<CachedBlock> {

	public BlockContextParameter(ResourceLocation name) {
		super(name);
	}

	@Override
	public boolean checkType(Predicate<Class<CachedBlock>> tester) {
		return tester.test(CachedBlock.class);
	}

	@Override
	public void addAsArgument(CommandBuildContext buildContext, CommandNode<CommandSourceStack> baseNode, CommandNode<CommandSourceStack> parameterNode) {

		var posNode = Commands.argument("pos", BlockPosArgument.blockPos())
			.redirect(baseNode, this::addToSource)
			.build();

		parameterNode.addChild(posNode);

	}

	CommandSourceStack addToSource(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

		CommandSourceStack source = context.getSource();
		BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");

		source.neo_apoli$getContextBuilder().withRequired(this, CachedBlock.fromLoadedPos(source.getLevel(), pos));
		return source;

	}

}
