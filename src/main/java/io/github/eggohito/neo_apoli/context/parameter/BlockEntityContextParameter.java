package io.github.eggohito.neo_apoli.context.parameter;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.context.ContextBuilderHolder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.commands.Commands.argument;

public class BlockEntityContextParameter extends ContextParameter<BlockEntity> {

	public BlockEntityContextParameter(ResourceLocation name) {
		super(name);
	}

	@Override
	public @NotNull Class<BlockEntity> typeClass() {
		return BlockEntity.class;
	}

	@Override
	public void addAsArgument(CommandBuildContext buildContext, CommandNode<CommandSourceStack> baseNode, CommandNode<CommandSourceStack> parameterNode) {

		CommandNode<CommandSourceStack> posNode = argument("pos", BlockPosArgument.blockPos())
			.redirect(baseNode, this::addBlockEntityToContext)
			.build();

		parameterNode.addChild(posNode);

	}

	protected CommandSourceStack addBlockEntityToContext(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

		CommandSourceStack source = context.getSource();
		BlockPos blockPos = BlockPosArgument.getLoadedBlockPos(context, "pos");

		((ContextBuilderHolder) source).neo_apoli$getContextBuilder().withNullable(this, source.getLevel().getBlockEntity(blockPos));
		return source;

	}

}
