package io.github.eggohito.neo_apoli.util.context.parameter.block;

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
import org.jetbrains.annotations.Nullable;

import static net.minecraft.commands.Commands.argument;

public class BlockPosContextKey extends TypedContextKey<BlockPos> {

	public BlockPosContextKey(ResourceLocation id) {
		super(id, BlockPos.class);
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

				((ContextBuilderHolder) source).neo_apoli$getContextBuilder().add(BlockPosContextKey.this, blockPos);
				return source;

			}

		};
	}

}
