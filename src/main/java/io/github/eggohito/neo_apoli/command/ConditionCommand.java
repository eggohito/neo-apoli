package io.github.eggohito.neo_apoli.command;

import com.google.gson.JsonElement;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.command.argument.ConditionArgumentType;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.util.JsonTextFormatter;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ConditionCommand {

	public static void register(CommandRegistryAccess registryAccess, CommandNode<ServerCommandSource> rootNode) {

		CommandNode<ServerCommandSource> baseNode = literal("condition")
			.requires(source -> source.hasPermissionLevel(2))
			.build();

		baseNode.addChild(DumpSubCommand.node(registryAccess));
		baseNode.addChild(TestSubCommand.node(registryAccess));

		rootNode.addChild(baseNode);

	}

	static final class DumpSubCommand {

		static CommandNode<ServerCommandSource> node(CommandRegistryAccess registryAccess) {

			var node = literal("dump")
				.then(argument("condition", ConditionArgumentType.condition(registryAccess))
					.executes(DumpSubCommand::withDefaultIndent)
					.then(argument("indent", IntegerArgumentType.integer(0))
						.executes(DumpSubCommand::withSpecificIndent)));

			return node.build();

		}

		static int withDefaultIndent(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
			return execute(commandContext, 4);
		}

		static int withSpecificIndent(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
			return execute(commandContext, IntegerArgumentType.getInteger(commandContext, "indent"));
		}

		static int execute(CommandContext<ServerCommandSource> commandContext, int indent) throws CommandSyntaxException {

			ServerCommandSource commandSource = commandContext.getSource();
			RegistryOps<JsonElement> ops = commandSource.getRegistryManager().getOps(JsonOps.INSTANCE);

			Condition condition = ConditionArgumentType.getCondition(commandContext, "condition");

			return switch (Condition.BASE_CODEC.encodeStart(ops, condition)) {
				case DataResult.Success<JsonElement> success -> {

					JsonElement jsonElement = success.value();
					commandSource.sendFeedback(() -> JsonTextFormatter.format(jsonElement, indent), false);

					yield jsonElement.toString().length();

				}
				case DataResult.Error<JsonElement> error ->
					throw MiscUtil.createCommandException(error::message);
			};

		}

	}

	//	TODO: Re-implement testing of conditions with dynamic context parameter arguments
	static final class TestSubCommand {

		static CommandNode<ServerCommandSource> node(CommandRegistryAccess registryAccess) {
			return literal("test").build();
		}

	}

}
