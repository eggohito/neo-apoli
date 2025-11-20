package io.github.eggohito.neo_apoli.util.context.parameter.block;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.duck.ServerContextBuilderHolder;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextParameter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.server.command.CommandManager.argument;

public class BlockPosContextParameter extends TypedContextParameter<BlockPos> {

	public BlockPosContextParameter(Identifier id) {
		super(id, BlockPos.class);
	}

	@Override
	public @Nullable CommandBuilder getCommandBuilder() {
		return new CommandBuilder() {

			@Override
			public void addArguments(CommandRegistryAccess registryAccess, CommandNode<ServerCommandSource> executeNode, CommandNode<ServerCommandSource> parameterNode) {

				CommandNode<ServerCommandSource> posNode = argument("pos", BlockPosArgumentType.blockPos())
					.redirect(executeNode, this::redirect)
					.build();

				parameterNode.addChild(posNode);

			}

			ServerCommandSource redirect(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

				ServerCommandSource source = context.getSource();
				BlockPos blockPos = BlockPosArgumentType.getLoadedBlockPos(context, "pos");

				((ServerContextBuilderHolder) source).neo_apoli$getBuilder().add(BlockPosContextParameter.this, blockPos);
				return source;

			}

		};
	}

}
