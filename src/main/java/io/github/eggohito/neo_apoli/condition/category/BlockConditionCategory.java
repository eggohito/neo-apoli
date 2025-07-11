package io.github.eggohito.neo_apoli.condition.category;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.command.argument.ConditionArgumentType;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.mixin.access.ExecuteCommandAccessor;
import io.github.eggohito.neo_apoli.mixin.access.ReloadableRegistriesAccessor;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.MiscUtil;
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
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

import static net.minecraft.server.command.CommandManager.argument;

public class BlockConditionCategory extends ConditionCategory<BlockCondition> {

	private static final Function<String, CommandBuilder> BUILDER_FACTORY = conditionKey -> new CommandBuilder() {

		@Override
		public ArgumentBuilder<ServerCommandSource, ?> addArguments(CommandNode<ServerCommandSource> root, CommandRegistryAccess registryAccess, ArgumentBuilder<ServerCommandSource, ?> builder, boolean positive) {
			return builder
				.then(ExecuteCommandAccessor.callAddConditionLogic(root, argument("pos", BlockPosArgumentType.blockPos()), positive, this::test));
		}

		public boolean test(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {

			ServerCommandSource commandSource = commandContext.getSource();
			ServerWorld serverWorld = commandSource.getWorld();

			BlockPos blockPos = BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos");

			BlockState blockState = serverWorld.getBlockState(blockPos);
			BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);

			BlockCondition blockCondition = ConditionArgumentType.getCondition(commandContext, conditionKey);
			ContextAware.ErrorReporter reporter = new ContextAware.ErrorReporter("{" + ConditionManager.getIdAsResult(blockCondition).mapOrElse(Identifier::toString, error -> blockCondition.toString()) + "}")
				.withContextType(ContextTypes.BLOCK)
				.withWrapperLookup(((ReloadableRegistriesAccessor.LookupAccessor) commandSource.getServer().getReloadableRegistries()).getRegistries());

			Context context = new Context.Builder(reporter.getContextType())
				.withReporter(reporter)
				.add(ContextParameters.POSITION, blockPos.toCenterPos())
				.add(ContextParameters.BLOCK_STATE, blockState)
				.addNullable(ContextParameters.BLOCK_ENTITY, blockEntity)
				.build(serverWorld);

			blockCondition.validate(reporter);

			if (reporter.hasAnyErrors()) {
				throw MiscUtil.createCommandException(Text.literal("Error(s) while validating block condition " + reporter.getErrorsAsString()));
			}

			else {

				boolean result = blockCondition.test(context);
				if (reporter.hasAnyErrors()) {
					throw MiscUtil.createCommandException(Text.literal("Error(s) while testing block condition " + reporter.getErrorsAsString()));
				}

				else {
					return result;
				}

			}

		}

	};

	BlockConditionCategory() {

	}

	@Override
	public RegistryKey<? extends Registry<BlockCondition>> registryRef() {
		return NeoApoliRegistryKeys.BLOCK_CONDITION;
	}

	@Override
	public @Nullable Function<String, CommandBuilder> commandBuilderFactory() {
		return BUILDER_FACTORY;
	}

	@Override
	public Codec<BlockCondition> baseCodec() {
		return BlockCondition.CODEC;
	}

	@Override
	public PacketCodec<RegistryByteBuf, BlockCondition> basePacketCodec() {
		return BlockCondition.PACKET_CODEC;
	}

	@Override
	public String toString() {
		return "Block condition";
	}

}
