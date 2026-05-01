package io.github.eggohito.neo_apoli.action.kind.custom;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.custom.item.ItemAction;
import io.github.eggohito.neo_apoli.action.kind.ActionKind;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;

import java.util.function.Function;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public enum ItemActionKind implements ActionKind<ItemAction> {

	INSTANCE;

	@Override
	public Function<String, CommandBuilder> commandBuilder() {
		return actionKey -> new CommandBuilder() {

			@Override
			public ArgumentBuilder<CommandSourceStack, ?> addArguments(CommandBuildContext buildContext, ArgumentBuilder<CommandSourceStack, ?> builder) {
				return builder
					.then(literal("block")
						.then(argument("pos", BlockPosArgument.blockPos())
							.then(argument("slot", SlotArgument.slot())
								.executes(this::executeFromBlock))))
					.then(literal("entity")
						.then(argument("target", EntityArgument.entity())
							.then(argument("slot", SlotArgument.slot())
								.executes(this::executeFromEntity))));
			}

			int executeFromBlock(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

				CommandSourceStack source = commandContext.getSource();
				ServerLevel serverLevel = source.getLevel();

				BlockPos blockPos = BlockPosArgument.getLoadedBlockPos(commandContext, "pos");
				int slot = SlotArgument.getSlot(commandContext, "slot");

				if (serverLevel.getBlockEntity(blockPos) instanceof Container container) {

					if (slot >= 0 && slot < container.getContainerSize()) {
						return execute(commandContext, SlotAccess.forContainer(container, slot));
					}

					else {
						throw MiscUtil.createCommandException(Component.translatable("commands.item.target.no_such_slot"));
					}

				}

				else {
					throw MiscUtil.createCommandException(Component.translatable("commands.item.target.not_a_container", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
				}

			}

			int executeFromEntity(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

				Entity target = EntityArgument.getEntity(commandContext, "target");
				int slot = SlotArgument.getSlot(commandContext, "slot");

				SlotAccess slotAccess = target.getSlot(slot);

				if (slotAccess == SlotAccess.NULL) {
					throw MiscUtil.createCommandException(Component.translatable("commands.item.target.no_such_slot", slot));
				}

				else {
					return execute(commandContext, slotAccess);
				}

			}

			int execute(CommandContext<CommandSourceStack> commandContext, SlotAccess slotAccess) throws CommandSyntaxException {
				return ItemActionKind.this.execute(
					commandContext,
					actionKey,
					action -> Util.getRegisteredName(NeoApoliRegistries.ITEM_ACTION_TYPE, action.getType()),
					builder -> builder.withRequired(NeoApoliContextParams.SLOT_ACCESS, slotAccess)
				);
			}

		};
	}

	@Override
	public ResourceKey<? extends Registry<ItemAction>> registryKey() {
		return NeoApoliRegistryKeys.ITEM_ACTION;
	}

	@Override
	public Codec<ItemAction> codec() {
		return ItemAction.CODEC;
	}

	@Override
	public String asDisplayString() {
		return "Item action";
	}

}
