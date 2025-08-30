package io.github.eggohito.neo_apoli.condition.category;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.command.argument.ConditionArgumentType;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.mixin.access.ReloadableRegistriesAccessor;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.context.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.SlotRangeArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SlotRange;
import net.minecraft.inventory.StackReference;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ItemConditionCategory extends ConditionCategory<ItemCondition> {

	private static final Function<String, CommandBuilder> BUILDER_FACTORY = conditionKey -> new CommandBuilder() {

		@Override
		public ArgumentBuilder<ServerCommandSource, ?> addArguments(Optional<CommandNode<ServerCommandSource>> root, CommandRegistryAccess registryAccess, ArgumentBuilder<ServerCommandSource, ?> builder, boolean positive) {
			return builder
				.then(literal("block")
					.then(argument("pos", BlockPosArgumentType.blockPos())
						.then(CommandBuilder.optionallyAddForkedConditionLogic(root, argument("slot", SlotRangeArgumentType.slotRange()), positive, this::testBlock))))
				.then(literal("entity")
					.then(argument("target", EntityArgumentType.entity())
						.then(CommandBuilder.optionallyAddForkedConditionLogic(root, argument("slot", SlotRangeArgumentType.slotRange()), positive, this::testEntity))));
		}

		public boolean testBlock(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {

			ServerCommandSource commandSource = commandContext.getSource();
			ServerWorld serverWorld = commandSource.getWorld();

			BlockPos blockPos = BlockPosArgumentType.getBlockPos(commandContext, "pos");
			SlotRange slotRange = SlotRangeArgumentType.getSlotRange(commandContext, "slot");

			List<Context> contexts = new ObjectArrayList<>();
			IntList invalidSlots = new IntArrayList();

			ItemCondition itemCondition = ConditionArgumentType.getCondition(commandContext, conditionKey, ItemCondition.class);
			ContextAware.ErrorReporter reporter = new ContextAware.ErrorReporter("{" + ConditionManager.getIdAsResult(itemCondition).mapOrElse(Identifier::toString, error -> itemCondition.toString()) + "}")
				.withContextType(ContextTypeUtil.merge(ContextTypes.GENERIC, ContextTypes.BLOCK, ContextTypes.ITEM))
				.withWrapperLookup(((ReloadableRegistriesAccessor.LookupAccessor) commandSource.getServer().getReloadableRegistries()).getRegistries());

			if (serverWorld.getBlockEntity(blockPos) instanceof Inventory inventory) {

				for (int slotId : slotRange.getSlotIds()) {

					if (slotId >= 0 && slotId < inventory.size()) {

						StackReference stackReference = StackReference.of(inventory, slotId);
						Context context = Context.builder(reporter)
							.add(ContextParameters.BLOCK_POS, blockPos)
							.add(ContextParameters.BLOCK_STATE, serverWorld.getBlockState(blockPos))
							.addNullable(ContextParameters.BLOCK_ENTITY, serverWorld.getBlockEntity(blockPos))
							.add(ContextParameters.STACK_REFERENCE, stackReference)
							.add(ContextParameters.ITEM_STACK, stackReference.get())
							.build(serverWorld);

						contexts.add(context);

					}

					else {
						invalidSlots.add(slotId);
					}

				}

				if (contexts.isEmpty()) {
					throw MiscUtil.createCommandException(Text.literal("Target doesn't have slots " + invalidSlots + " from slot range " + slotRange.asString() + "!"));
				}

				else {
					return this.test(reporter, itemCondition, contexts);
				}

			}

			else {
				throw MiscUtil.createCommandException(Text.translatable("commands.item.target.not_a_container", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
			}

		}

		public boolean testEntity(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {

			Entity target = EntityArgumentType.getEntity(commandContext, "target");
			SlotRange slotRange = SlotRangeArgumentType.getSlotRange(commandContext, "slot");

			List<Context> contexts = new ObjectArrayList<>();
			IntList invalidSlots = new IntArrayList();

			ItemCondition itemCondition = ConditionArgumentType.getCondition(commandContext, conditionKey, ItemCondition.class);
			ContextAware.ErrorReporter reporter = new ContextAware.ErrorReporter("{" + ConditionManager.getIdAsResult(itemCondition).mapOrElse(Identifier::toString, error -> itemCondition.toString()) + "}")
				.withContextType(ContextTypeUtil.merge(ContextTypes.GENERIC, ContextTypes.ITEM))
				.withWrapperLookup(((ReloadableRegistriesAccessor.LookupAccessor) commandContext.getSource().getServer().getReloadableRegistries()).getRegistries());

			for (int slotId : slotRange.getSlotIds()) {

				StackReference stackReference = target.getStackReference(slotId);
				if (stackReference != StackReference.EMPTY) {

					Context context = Context.builder(reporter)
						.add(ContextParameters.ENTITY, target)
						.add(ContextParameters.ENTITY_POS, target.getPos())
						.add(ContextParameters.STACK_REFERENCE, stackReference)
						.add(ContextParameters.ITEM_STACK, stackReference.get())
						.build(commandContext.getSource().getWorld());

					contexts.add(context);

				}

				else {
					invalidSlots.add(slotId);
				}

			}

			if (contexts.isEmpty()) {
				throw MiscUtil.createCommandException(Text.literal("Target doesn't have slots " + invalidSlots + " from slot range " + slotRange.asString() + "!"));
			}

			else {
				return this.test(reporter, itemCondition, contexts);
			}

		}

		public boolean test(ContextAware.ErrorReporter reporter, ItemCondition itemCondition, List<Context> contexts) throws CommandSyntaxException {

			itemCondition.validate(reporter);
			if (reporter.hasAnyErrors()) {
				throw MiscUtil.createCommandException(Text.literal("Error(s) while validating item condition " + reporter.getErrorsAsString()));
			}

			for (var context : contexts) {

				boolean result = itemCondition.test(context);

				if (!reporter.hasAnyErrors() && result) {
					return true;
				}

			}

			if (reporter.hasAnyErrors()) {
				throw MiscUtil.createCommandException(Text.literal("Error(s) while testing item condition " + reporter.getErrorsAsString()));
			}

			else {
				return false;
			}

		}

	};

	ItemConditionCategory() {

	}

	@Override
	public RegistryKey<? extends Registry<ItemCondition>> registryRef() {
		return NeoApoliRegistryKeys.ITEM_CONDITION;
	}

	@Override
	public @Nullable Function<String, CommandBuilder> commandBuilderFactory() {
		return BUILDER_FACTORY;
	}

	@Override
	public Codec<ItemCondition> codec() {
		return ItemCondition.CODEC;
	}

	@Override
	public MapCodec<ItemCondition> mapCodec() {
		return ItemCondition.MAP_CODEC;
	}

	@Override
	public PacketCodec<RegistryByteBuf, ItemCondition> packetCodec() {
		return ItemCondition.PACKET_CODEC;
	}

	@Override
	public String toString() {
		return "Item condition";
	}

}
