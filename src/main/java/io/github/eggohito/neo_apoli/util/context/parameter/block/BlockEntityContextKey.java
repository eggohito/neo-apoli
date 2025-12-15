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
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.commands.Commands.argument;

public class BlockEntityContextKey extends TypedContextKey<BlockEntity> {

	public BlockEntityContextKey(ResourceLocation id) {
		super(id, BlockEntity.class);
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

				BlockEntity blockEntity = source.getLevel().getBlockEntity(blockPos);
				((ContextBuilderHolder) source).neo_apoli$getContextBuilder().addNullable(BlockEntityContextKey.this, blockEntity);

				return source;

			}

		};
	}

}
