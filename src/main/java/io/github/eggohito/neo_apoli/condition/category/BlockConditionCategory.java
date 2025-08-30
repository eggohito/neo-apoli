package io.github.eggohito.neo_apoli.condition.category;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.command.argument.ConditionArgumentType;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.mixin.access.ReloadableRegistriesAccessor;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.context.*;
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

import java.util.Optional;
import java.util.function.Function;

import static net.minecraft.server.command.CommandManager.argument;

public class BlockConditionCategory extends ConditionCategory<BlockCondition> {

	private static final Function<String, CommandBuilder> BUILDER_FACTORY = conditionKey -> new CommandBuilder() {

		@Override
		public ArgumentBuilder<ServerCommandSource, ?> addArguments(Optional<CommandNode<ServerCommandSource>> root, CommandRegistryAccess registryAccess, ArgumentBuilder<ServerCommandSource, ?> builder, boolean positive) {
			return builder
				.then(CommandBuilder.optionallyAddForkedConditionLogic(root, argument("pos", BlockPosArgumentType.blockPos()), positive, this::test));
		}

		public boolean test(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {

			ServerCommandSource commandSource = commandContext.getSource();
			ServerWorld serverWorld = commandSource.getWorld();

			BlockPos blockPos = BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos");
			BlockCondition blockCondition = ConditionArgumentType.getCondition(commandContext, conditionKey, BlockCondition.class);

			ContextAware.ErrorReporter reporter = new ContextAware.ErrorReporter("{" + ConditionManager.getIdAsResult(blockCondition).mapOrElse(Identifier::toString, error -> blockCondition.toString()) + "}")
				.withContextType(ContextTypeUtil.merge(ContextTypes.GENERIC, ContextTypes.BLOCK))
				.withWrapperLookup(((ReloadableRegistriesAccessor.LookupAccessor) commandSource.getServer().getReloadableRegistries()).getRegistries());

			Context context = Context.builder(reporter)
				.add(ContextParameters.BLOCK_POS, blockPos)
				.add(ContextParameters.BLOCK_STATE, serverWorld.getBlockState(blockPos))
				.addNullable(ContextParameters.BLOCK_ENTITY, serverWorld.getBlockEntity(blockPos))
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
	public Codec<BlockCondition> codec() {
		return BlockCondition.CODEC;
	}

	@Override
	public MapCodec<BlockCondition> mapCodec() {
		return BlockCondition.MAP_CODEC;
	}

	@Override
	public PacketCodec<RegistryByteBuf, BlockCondition> packetCodec() {
		return BlockCondition.PACKET_CODEC;
	}

	@Override
	public String toString() {
		return "Block condition";
	}

}
