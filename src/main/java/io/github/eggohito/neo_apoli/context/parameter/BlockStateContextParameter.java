package io.github.eggohito.neo_apoli.context.parameter;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.commands.Commands.argument;

public class BlockStateContextParameter extends ContextParameter<BlockState> {

	public BlockStateContextParameter(ResourceLocation name) {
		super(name);
	}

	@Override
	public @NotNull Class<BlockState> typeClass() {
		return BlockState.class;
	}

	@Override
	public void addAsArgument(CommandBuildContext buildContext, CommandNode<CommandSourceStack> baseNode, CommandNode<CommandSourceStack> parameterNode) {

		CommandNode<CommandSourceStack> posNode = argument("pos", BlockPosArgument.blockPos())
			.redirect(baseNode, this::addBlockStateToContext)
			.build();

		parameterNode.addChild(posNode);

	}

	protected CommandSourceStack addBlockStateToContext(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

		CommandSourceStack source = context.getSource();
		BlockPos blockPos = BlockPosArgument.getLoadedBlockPos(context, "pos");

		source.neo_apoli$getContextBuilder().withRequired(this, source.getLevel().getBlockState(blockPos));
		return source;

	}

}
