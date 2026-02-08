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
import io.github.eggohito.neo_apoli.command.argument.ActionArgument;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.JsonTextFormatter;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.Reporter;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ActionCommand {

	public static void register(CommandBuildContext registryAccess, CommandNode<CommandSourceStack> rootNode) {

		CommandNode<CommandSourceStack> baseNode = literal("action")
			.requires(source -> source.hasPermission(2))
			.build();

		baseNode.addChild(DumpSubCommand.node(registryAccess));
		baseNode.addChild(ExecuteSubCommand.node(registryAccess));

		rootNode.addChild(baseNode);

	}

	static final class DumpSubCommand {

		static CommandNode<CommandSourceStack> node(CommandBuildContext registryAccess) {

			var node = literal("dump")
				.then(argument("action", ActionArgument.action(registryAccess))
					.executes(DumpSubCommand::withDefaultIndent)
					.then(argument("indent", IntegerArgumentType.integer(0))
						.executes(DumpSubCommand::withSpecificIndent)));

			return node.build();

		}

		private static int withDefaultIndent(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(commandContext, 4);
		}

		private static int withSpecificIndent(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(commandContext, IntegerArgumentType.getInteger(commandContext, "indent"));
		}

		private static int execute(CommandContext<CommandSourceStack> commandContext, int indent) throws CommandSyntaxException {

			CommandSourceStack commandSource = commandContext.getSource();
			RegistryOps<JsonElement> ops = commandSource.registryAccess().createSerializationContext(JsonOps.INSTANCE);

			Action action = ActionArgument.getAction(commandContext, "action");

			return switch (Action.CODEC.encodeStart(ops, action)) {
				case DataResult.Success<JsonElement> success -> {

					JsonElement jsonElement = success.value();
					commandSource.sendSuccess(() -> JsonTextFormatter.format(jsonElement, indent), false);

					yield jsonElement.toString().length();

				}
				case DataResult.Error<JsonElement> error ->
					throw MiscUtil.createCommandException(error::message);
			};

		}

	}

	static final class ExecuteSubCommand {

		static CommandNode<CommandSourceStack> node(CommandBuildContext buildContext) {

			var executeNode = literal("execute").build();
			var withNode = literal("with").build();
			var actionNode = argument("action", ActionArgument.inlineAction(buildContext)).executes(ExecuteSubCommand::execute).build();

			NeoApoliContextParams.addAllAsArguments(buildContext, executeNode, withNode, actionNode);
			return executeNode;

		}

		static int execute(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

			CommandSourceStack source = commandContext.getSource();
			Context.Builder builder = source.neo_apoli$getContextBuilder();

			Action action = ActionArgument.getAction(commandContext, "action");
			String path = ActionManager.getIdAsResult(action).mapOrElse(id -> "{\"" + id + "\"}", error -> "{type: \"" + Util.getRegisteredName(NeoApoliRegistries.ACTION_TYPE, action.getType()) + "\"");

			Reporter reporter = new Reporter(path);
			Context.Validator validator = new Context.Validator(LootContextParamSets.EMPTY, reporter);

			action.validate(validator);
			var validationException = reporter.getErrorsFlattened()
				.map(error -> Component.literal("Found errors while validating action: ").append(error))
				.map(MiscUtil::createCommandException);

			if (validationException.isPresent()) {
				throw validationException.get();
			}

			Context context = builder
				.withReporter(reporter)
				.build(source.getLevel());

			action.execute(context);
			var executionException = reporter.getErrorsFlattened()
				.map(error -> Component.literal("Found errors while executing action: ").append(error))
				.map(MiscUtil::createCommandException);

			if (executionException.isPresent()) {
				throw executionException.get();
			}

			source.sendSuccess(() -> Component.nullToEmpty("Successfully executed action!"), true);
			return 1;

		}

	}

}
