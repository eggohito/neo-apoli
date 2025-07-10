package io.github.eggohito.neo_apoli.action.category;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.command.argument.ActionArgumentType;
import io.github.eggohito.neo_apoli.mixin.access.ReloadableRegistriesAccessor;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.function.Function;

import static net.minecraft.server.command.CommandManager.argument;

public final class BlockActionCategory extends ActionCategory<BlockAction> {

	private static final Function<String, CommandBuilder> BUILDER_FACTORY = actionKey -> new CommandBuilder() {

		@Override
		public ArgumentBuilder<ServerCommandSource, ?> addArguments(CommandRegistryAccess registryAccess, ArgumentBuilder<ServerCommandSource, ?> builder) {
			return builder.then(
				argument("pos", BlockPosArgumentType.blockPos())
					.executes(this::execute)
			);
		}

		public int execute(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {

			ServerCommandSource commandSource = commandContext.getSource();
			ServerWorld serverWorld = commandSource.getWorld();

			BlockPos blockPos = BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos");

			BlockState blockState = serverWorld.getBlockState(blockPos);
			BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);

			BlockAction blockAction = ActionArgumentType.getAction(commandContext, actionKey);
			ContextAware.ErrorReporter reporter = new ContextAware.ErrorReporter("{" + ActionManager.getIdAsResult(blockAction).mapOrElse(Identifier::toString, error -> blockAction.toString()) + "}")
				.withContextType(ContextTypes.BLOCK)
				.withWrapperLookup(((ReloadableRegistriesAccessor.LookupAccessor) commandSource.getServer().getReloadableRegistries()).getRegistries());

			Context context = new Context.Builder(reporter.getContextType())
				.withReporter(reporter)
				.add(ContextParameters.POSITION, blockPos.toCenterPos())
				.add(ContextParameters.BLOCK_STATE, blockState)
				.addNullable(ContextParameters.BLOCK_ENTITY, blockEntity)
				.build(serverWorld);

			blockAction.validate(reporter);

			if (reporter.hasAnyErrors()) {
				commandSource.sendError(Text.literal("Error validating block action due to error(s) " + reporter.getErrorsAsString()));
				return 0;
			}

			else {

				blockAction.execute(context);

				if (reporter.hasAnyErrors()) {
					commandSource.sendError(Text.literal("Error(s) while executing block action " + reporter.getErrorsAsString()));
					return 0;
				}

				else {
					commandSource.sendFeedback(() -> Text.literal("Successfully executed block action!"), true);
					return 1;
				}

			}

		}

	};

	BlockActionCategory() {

	}

	@Override
	public RegistryKey<? extends Registry<BlockAction>> registryRef() {
		return NeoApoliRegistryKeys.BLOCK_ACTION;
	}

	@Override
	public Function<String, CommandBuilder> commandBuilderFactory() {
		return BUILDER_FACTORY;
	}

	@Override
	public Codec<BlockAction> baseCodec() {
		return BlockAction.CODEC;
	}

	@Override
	public PacketCodec<RegistryByteBuf, BlockAction> basePacketCodec() {
		return BlockAction.PACKET_CODEC;
	}

	@Override
	public String toString() {
		return "Block action";
	}

}
