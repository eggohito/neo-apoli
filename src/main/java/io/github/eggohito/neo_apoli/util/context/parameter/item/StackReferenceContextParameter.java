package io.github.eggohito.neo_apoli.util.context.parameter.item;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.duck.ServerContextBuilderHolder;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextParameter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemSlotArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class StackReferenceContextParameter extends TypedContextParameter<StackReference> {

	public StackReferenceContextParameter(Identifier id) {
		super(id, StackReference.class);
	}

	@Override
	public @Nullable CommandBuilder getCommandBuilder() {
		return new CommandBuilder() {

			@Override
			public void addArguments(CommandRegistryAccess registryAccess, CommandNode<ServerCommandSource> baseNode, CommandNode<ServerCommandSource> parameterNode) {

				CommandNode<ServerCommandSource> blockNode = literal("block")
					.then(argument("pos", BlockPosArgumentType.blockPos())
						.then(argument("slot", ItemSlotArgumentType.itemSlot())
							.redirect(baseNode, this::redirectBlock))).build();
				CommandNode<ServerCommandSource> entityNode = literal("entity")
					.then(argument("target", EntityArgumentType.entity())
						.then(argument("slot", ItemSlotArgumentType.itemSlot())
							.redirect(baseNode, this::redirectEntity))).build();

				parameterNode.addChild(blockNode);
				parameterNode.addChild(entityNode);

			}

			ServerCommandSource redirectBlock(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

				ServerCommandSource source = context.getSource();
				ServerWorld world = source.getWorld();

				BlockPos blockPos = BlockPosArgumentType.getLoadedBlockPos(context, "pos");
				int slot = ItemSlotArgumentType.getItemSlot(context, "slot");

				if (world.getBlockEntity(blockPos) instanceof Inventory inventory) {

					if (slot >= 0 && slot < inventory.size()) {
						((ServerContextBuilderHolder) source).neo_apoli$getBuilder().add(StackReferenceContextParameter.this, StackReference.of(inventory, slot));
					}

					else {
						throw MiscUtil.createCommandException(Text.translatable("commands.item.target.no_such_slot", slot));
					}

				}

				else {
					throw MiscUtil.createCommandException(Text.translatable("commands.item.target.not_a_container", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
				}

				return source;

			}

			ServerCommandSource redirectEntity(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

				ServerCommandSource source = context.getSource();

				Entity target = EntityArgumentType.getEntity(context, "target");
				int slot = ItemSlotArgumentType.getItemSlot(context, "slot");

				StackReference stackReference = target.getStackReference(slot);

				if (stackReference != StackReference.EMPTY) {
					((ServerContextBuilderHolder) source).neo_apoli$getBuilder().add(StackReferenceContextParameter.this, stackReference);
				}

				else {
					throw MiscUtil.createCommandException(Text.translatable("commands.item.target.no_such_slot", slot));
				}

				return source;

			}

		};
	}

}
