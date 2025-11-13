package io.github.eggohito.neo_apoli.command;

import com.google.gson.JsonElement;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.command.argument.ActionArgumentType;
import io.github.eggohito.neo_apoli.util.JsonTextFormatter;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ActionCommand {

	public static void register(CommandRegistryAccess registryAccess, CommandNode<ServerCommandSource> rootNode) {

		CommandNode<ServerCommandSource> baseNode = literal("action")
			.requires(source -> source.hasPermissionLevel(2))
			.build();

		baseNode.addChild(DumpSubCommand.node(registryAccess));
		baseNode.addChild(ExecuteSubCommand.node(registryAccess));

		rootNode.addChild(baseNode);

	}

	static final class DumpSubCommand {

		static CommandNode<ServerCommandSource> node(CommandRegistryAccess registryAccess) {

			var node = literal("dump")
				.then(argument("action", ActionArgumentType.action(registryAccess, false))
					.executes(DumpSubCommand::withDefaultIndent)
					.then(argument("indent", IntegerArgumentType.integer(0))
						.executes(DumpSubCommand::withSpecificIndent)));

			return node.build();

		}

		private static int withDefaultIndent(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
			return execute(commandContext, 4);
		}

		private static int withSpecificIndent(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
			return execute(commandContext, IntegerArgumentType.getInteger(commandContext, "indent"));
		}

		private static int execute(CommandContext<ServerCommandSource> commandContext, int indent) throws CommandSyntaxException {

			ServerCommandSource commandSource = commandContext.getSource();
			RegistryOps<JsonElement> ops = commandSource.getRegistryManager().getOps(JsonOps.INSTANCE);

			Action action = ActionArgumentType.getAction(commandContext, "action");

			return switch (Action.BASE_CODEC.encodeStart(ops, action)) {
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

	//	TODO: Re-implement execution of actions with dynamic context parameter arguments
	static final class ExecuteSubCommand {

		static CommandNode<ServerCommandSource> node(CommandRegistryAccess registryAccess) {
			return literal("execute").build();
		}

	}

}
