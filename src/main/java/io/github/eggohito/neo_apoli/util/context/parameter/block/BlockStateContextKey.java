package io.github.eggohito.neo_apoli.util.context.parameter.block;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.duck.ServerContextBuilderHolder;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.commands.Commands.argument;

public class BlockStateContextKey extends TypedContextKey<BlockState> {

	public BlockStateContextKey(ResourceLocation id) {
		super(id, BlockState.class);
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

				BlockState blockEntity = source.getLevel().getBlockState(blockPos);
				((ServerContextBuilderHolder) source).neo_apoli$getBuilder().add(BlockStateContextKey.this, blockEntity);

				return source;

			}

		};
	}

}
