package io.github.eggohito.neo_apoli.command;

import com.google.gson.JsonElement;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.command.argument.ActionArgumentType;
import io.github.eggohito.neo_apoli.duck.ServerContextBuilderHolder;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.JsonTextFormatter;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextTypes;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextParameter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
				.then(argument("action", ActionArgumentType.action(registryAccess))
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

			return switch (Action.CODEC.encodeStart(ops, action)) {
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

	static final class ExecuteSubCommand {

		static CommandNode<ServerCommandSource> node(CommandRegistryAccess registryAccess) {

			CommandNode<ServerCommandSource> executeNode = literal("execute").build();
			CommandNode<ServerCommandSource> withNode = literal("with").build();
			CommandNode<ServerCommandSource> onNode = literal("on")
				.then(argument("action", ActionArgumentType.inlineAction(registryAccess))
					.executes(ExecuteSubCommand::execute)).build();

			for (var parameter : NeoApoliRegistries.TYPED_CONTEXT_PARAMETER) {

				String id = parameter.getId().toString();
				TypedContextParameter.CommandBuilder parameterCommandBuilder = parameter.getCommandBuilder();

				if (parameterCommandBuilder == null) {
					continue;
				}

				CommandNode<ServerCommandSource> parameterNode = literal(id).build();
				parameterCommandBuilder.addArguments(registryAccess, executeNode, parameterNode);

				withNode.addChild(parameterNode);

			}

			executeNode.addChild(withNode);
			executeNode.addChild(onNode);

			return executeNode;

		}

		static int execute(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {

			ServerCommandSource source = commandContext.getSource();
			ServerContext.Builder builder = ((ServerContextBuilderHolder) source).neo_apoli$getBuilder();

			Action action = ActionArgumentType.getAction(commandContext, "action");
			String display = action.asDisplayString(false);

			try {

				String rootPath = ActionManager.getIdAsResult(action).mapOrElse(
					id -> "{\"" + id + "\"}",
					error -> "{type: \"" + RegistryUtil.getId(NeoApoliRegistries.ACTION_TYPE, action.getType()) + "\", ...}"
				);

				ContextAware.ErrorReporter reporter = new ContextAware.ErrorReporter(NeoApoliContextTypes.ANY, rootPath);
				ServerContext serverContext = builder
					.withReporter(reporter)
					.build(source.getWorld());

				action.validate(reporter);

				if (reporter.hasAnyErrors()) {
					throw MiscUtil.createCommandException(Text.literal("Found errors when validating " + display + ": ").append(reporter.getErrorsAsString()));
				}

				action.execute(serverContext);

				if (reporter.hasAnyErrors()) {
					source.sendFeedback(() -> Text.literal("").append("Warnings found when executing " + display + ": ").formatted(Formatting.YELLOW).append(reporter.getErrorsAsString()), false);
					return 0;
				}

				else {
					source.sendFeedback(() -> Text.of("Successfully executed " + display + "!"), true);
					return 1;
				}

			}

			catch (Exception e) {
				throw MiscUtil.createCommandException(() -> "Error executing " + display + ": " + e.getMessage());
			}

		}

	}

}
