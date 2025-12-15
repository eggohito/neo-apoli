package io.github.eggohito.neo_apoli.util.context.parameter.fluid;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.duck.ContextBuilderHolder;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.commands.Commands.argument;

public class FluidStateContextKey extends TypedContextKey<FluidState> {

	public FluidStateContextKey(ResourceLocation id) {
		super(id, FluidState.class);
	}

	@Override
	public @Nullable CommandBuilder getCommandBuilder() {
		return new CommandBuilder() {

			@Override
			public void addArguments(CommandBuildContext registryAccess, CommandNode<CommandSourceStack> baseNode, CommandNode<CommandSourceStack> parameterNode) {

				CommandNode<CommandSourceStack> posNode = argument("pos", BlockPosArgument.blockPos())
					.redirect(baseNode, this::redirect)
					.build();

				parameterNode.addChild(posNode);

			}

			CommandSourceStack redirect(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

				CommandSourceStack source = context.getSource();
				BlockPos blockPos = BlockPosArgument.getLoadedBlockPos(context, "pos");

				FluidState fluidState = source.getLevel().getFluidState(blockPos);
				((ContextBuilderHolder) source).neo_apoli$getContextBuilder().add(FluidStateContextKey.this, fluidState);

				return source;

			}

		};
	}

}
