package io.github.eggohito.neo_apoli.context.parameter;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.context.ContextBuilderHolder;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ItemStackContextParameter extends ContextParameter<ItemStack> {

	public ItemStackContextParameter(ResourceLocation name) {
		super(name);
	}

	@Override
	public @NotNull Class<ItemStack> typeClass() {
		return ItemStack.class;
	}

	@Override
	public void addAsArgument(CommandBuildContext buildContext, CommandNode<CommandSourceStack> baseNode, CommandNode<CommandSourceStack> parameterNode) {

		var withNode = literal("with").build();
		var fromNode = literal("from").build();

		var itemNode = argument("item", ItemArgument.item(buildContext))
			.then(argument("count", IntegerArgumentType.integer(1))
				.redirect(baseNode, this::addDirectItemToSourceWithCount)).build();
		var blockNode = literal("block")
			.then(argument("pos", BlockPosArgument.blockPos())
				.then(argument("slot", SlotArgument.slot())
					.redirect(baseNode, this::addItemFromBlockToSource))).build();
		var entityNode = literal("entity")
			.then(argument("target", EntityArgument.entity())
				.then(argument("slot", SlotArgument.slot())
					.redirect(baseNode, this::addItemFromEntityToSource))).build();

		withNode.addChild(itemNode);
		fromNode.addChild(blockNode);
		fromNode.addChild(entityNode);

		parameterNode.addChild(withNode);
		parameterNode.addChild(fromNode);

	}

	private CommandSourceStack addDirectItemToSourceWithCount(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

		CommandSourceStack source = context.getSource();
		ItemInput item = ItemArgument.getItem(context, "item");

		int count = IntegerArgumentType.getInteger(context, "count");
		((ContextBuilderHolder) source).neo_apoli$getContextBuilder().withRequired(this, item.createItemStack(count, false));

		return source;

	}

	private CommandSourceStack addItemFromBlockToSource(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

		CommandSourceStack source = context.getSource();
		ServerLevel level = source.getLevel();

		BlockPos blockPos = BlockPosArgument.getLoadedBlockPos(context, "pos");
		int slot = SlotArgument.getSlot(context, "slot");

		if (level.getBlockEntity(blockPos) instanceof Container container) {

			if (slot >= 0 && slot < container.getContainerSize()) {
				((ContextBuilderHolder) source).neo_apoli$getContextBuilder().withRequired(this, container.getItem(slot));
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

	private CommandSourceStack addItemFromEntityToSource(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

		CommandSourceStack source = context.getSource();
		Entity target = EntityArgument.getEntity(context, "target");

		int slot = SlotArgument.getSlot(context, "slot");
		SlotAccess slotAccess = target.getSlot(slot);

		if (slotAccess == SlotAccess.NULL) {
			throw MiscUtil.createCommandException(Component.translatable("commands.item.target.no_such_slot", slot));
		}

		((ContextBuilderHolder) source).neo_apoli$getContextBuilder().withRequired(this, slotAccess.get());
		return source;

	}

}
