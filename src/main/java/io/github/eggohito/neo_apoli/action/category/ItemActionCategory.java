package io.github.eggohito.neo_apoli.action.category;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.command.argument.ActionArgumentType;
import io.github.eggohito.neo_apoli.mixin.access.ReloadableRegistriesAccessor;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.function.Function;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ItemActionCategory extends ActionCategory<ItemAction> {

	private static final Function<String, CommandBuilder> BUILDER_FACTORY = actionKey -> new CommandBuilder() {

		@Override
		public ArgumentBuilder<ServerCommandSource, ?> addArguments(CommandRegistryAccess registryAccess, ArgumentBuilder<ServerCommandSource, ?> builder) {
			return builder
				.then(literal("block")
					.then(argument("pos", BlockPosArgumentType.blockPos())
						.then(argument("slot", SlotRangeArgumentType.slotRange())
							.executes(this::executeBlock))))
				.then(literal("entity")
					.then(argument("target", EntityArgumentType.entity())
						.then(argument("slot", SlotRangeArgumentType.slotRange())
							.executes(this::executeEntity))));
		}

		public int executeBlock(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {

			ServerCommandSource commandSource = commandContext.getSource();
			ServerWorld serverWorld = commandSource.getWorld();

			BlockPos blockPos = BlockPosArgumentType.getBlockPos(commandContext, "pos");
			SlotRange slotRange = SlotRangeArgumentType.getSlotRange(commandContext, "slot");

			List<Context> contexts = new ObjectArrayList<>();
			IntList invalidSlots = new IntArrayList();

			ItemAction itemAction = ActionArgumentType.getAction(commandContext, actionKey, ItemAction.class);
			ContextAware.ErrorReporter reporter = new ContextAware.ErrorReporter("{" + ActionManager.getIdAsResult(itemAction).mapOrElse(Identifier::toString, error -> itemAction.toString()) + "}")
				.withContextType(ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BLOCK, ContextTypes.ITEM))
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
					return this.execute(commandSource, reporter, itemAction, contexts);
				}

			}

			else {
				throw MiscUtil.createCommandException(Text.translatable("commands.item.target.not_a_container", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
			}

		}

		public int executeEntity(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {

			Entity target = EntityArgumentType.getEntity(commandContext, "target");
			SlotRange slotRange = SlotRangeArgumentType.getSlotRange(commandContext, "slot");

			List<Context> contexts = new ObjectArrayList<>();
			IntList invalidSlots = new IntArrayList();

			ItemAction itemAction = ActionArgumentType.getAction(commandContext, actionKey, ItemAction.class);
			ContextAware.ErrorReporter reporter = new ContextAware.ErrorReporter("{" + ActionManager.getIdAsResult(itemAction).mapOrElse(Identifier::toString, error -> itemAction.toString()) + "}")
				.withContextType(ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ITEM))
				.withWrapperLookup(((ReloadableRegistriesAccessor.LookupAccessor) commandContext.getSource().getServer().getReloadableRegistries()).getRegistries());

			for (int slotId : slotRange.getSlotIds()) {

				StackReference stackReference = target.getStackReference(slotId);

				if (stackReference != StackReference.EMPTY) {

					Context context = new Context.Builder(reporter.getContextType())
						.withReporter(reporter)
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

				int result = this.execute(commandContext.getSource(), reporter, itemAction, contexts);
				if (result > 0 && target instanceof ServerPlayerEntity serverPlayer) {
					serverPlayer.currentScreenHandler.sendContentUpdates();
				}

				return result;

			}

		}

		public int execute(ServerCommandSource commandSource, ContextAware.ErrorReporter reporter, ItemAction itemAction, List<Context> contexts) {

			itemAction.validate(reporter);

			if (reporter.hasErrors()) {
				commandSource.sendError(Text.literal("Error validating item action due to error(s) " + reporter.getErrorsAsString()));
				return 0;
			}

			else {

				for (var context : contexts) {
					itemAction.execute(context);
				}

				if (reporter.hasAnyErrors()) {
					commandSource.sendError(Text.literal("Error(s) while executing item action " + reporter.getErrorsAsString()));
					return 0;
				}

				else {
					commandSource.sendFeedback(() -> Text.literal("Successfully executed item action!"), true);
					return 1;
				}

			}

		}

	};

	ItemActionCategory() {

	}

	@Override
	public RegistryKey<? extends Registry<ItemAction>> registryRef() {
		return NeoApoliRegistryKeys.ITEM_ACTION;
	}

	@Override
	public Function<String, CommandBuilder> commandBuilderFactory() {
		return BUILDER_FACTORY;
	}

	@Override
	public Codec<ItemAction> codec() {
		return ItemAction.CODEC;
	}

	@Override
	public MapCodec<ItemAction> mapCodec() {
		return ItemAction.MAP_CODEC;
	}

	@Override
	public PacketCodec<RegistryByteBuf, ItemAction> packetCodec() {
		return ItemAction.PACKET_CODEC;
	}

	@Override
	public String toString() {
		return "Item action";
	}

}
