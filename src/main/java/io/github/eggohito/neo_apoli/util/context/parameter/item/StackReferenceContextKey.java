package io.github.eggohito.neo_apoli.util.context.parameter.item;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.duck.ContextBuilderHolder;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class StackReferenceContextKey extends TypedContextKey<SlotAccess> {

	public StackReferenceContextKey(ResourceLocation id) {
		super(id, SlotAccess.class);
	}

	@Override
	public @Nullable CommandBuilder getCommandBuilder() {
		return new CommandBuilder() {

			@Override
			public void addArguments(CommandBuildContext registryAccess, CommandNode<CommandSourceStack> baseNode, CommandNode<CommandSourceStack> parameterNode) {

				CommandNode<CommandSourceStack> blockNode = literal("block")
					.then(argument("pos", BlockPosArgument.blockPos())
						.then(argument("slot", SlotArgument.slot())
							.redirect(baseNode, this::redirectBlock))).build();
				CommandNode<CommandSourceStack> entityNode = literal("entity")
					.then(argument("target", EntityArgument.entity())
						.then(argument("slot", SlotArgument.slot())
							.redirect(baseNode, this::redirectEntity))).build();

				parameterNode.addChild(blockNode);
				parameterNode.addChild(entityNode);

			}

			CommandSourceStack redirectBlock(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

				CommandSourceStack source = context.getSource();
				ServerLevel world = source.getLevel();

				BlockPos blockPos = BlockPosArgument.getLoadedBlockPos(context, "pos");
				int slot = SlotArgument.getSlot(context, "slot");

				if (world.getBlockEntity(blockPos) instanceof Container inventory) {

					if (slot >= 0 && slot < inventory.getContainerSize()) {
						((ContextBuilderHolder) source).neo_apoli$getContextBuilder().add(StackReferenceContextKey.this, SlotAccess.forContainer(inventory, slot));
					}

					else {
						throw MiscUtil.createCommandException(Component.translatable("commands.item.target.no_such_slot", slot));
					}

				}

				else {
					throw MiscUtil.createCommandException(Component.translatable("commands.item.target.not_a_container", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
				}

				return source;

			}

			CommandSourceStack redirectEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

				CommandSourceStack source = context.getSource();

				Entity target = EntityArgument.getEntity(context, "target");
				int slot = SlotArgument.getSlot(context, "slot");

				SlotAccess stackReference = target.getSlot(slot);

				if (stackReference != SlotAccess.NULL) {
					((ContextBuilderHolder) source).neo_apoli$getContextBuilder().add(StackReferenceContextKey.this, stackReference);
				}

				else {
					throw MiscUtil.createCommandException(Component.translatable("commands.item.target.no_such_slot", slot));
				}

				return source;

			}

		};
	}

}
