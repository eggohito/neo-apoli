package io.github.eggohito.neo_apoli.context.parameter;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.commands.Commands.argument;

public class FluidStateContextParameter extends ContextParameter<FluidState> {

	public FluidStateContextParameter(ResourceLocation name) {
		super(name);
	}

	@Override
	public @NotNull Class<FluidState> typeClass() {
		return FluidState.class;
	}

	@Override
	public void addAsArgument(CommandBuildContext buildContext, CommandNode<CommandSourceStack> baseNode, CommandNode<CommandSourceStack> parameterNode) {

		CommandNode<CommandSourceStack> posNode = argument("pos", BlockPosArgument.blockPos())
			.redirect(baseNode, this::addFluidStateToContext)
			.build();

		parameterNode.addChild(posNode);

	}

	protected CommandSourceStack addFluidStateToContext(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

		CommandSourceStack source = context.getSource();
		BlockPos blockPos = BlockPosArgument.getLoadedBlockPos(context, "pos");

		source.neo_apoli$getContextBuilder().withRequired(this, source.getLevel().getFluidState(blockPos));
		return source;

	}

}
