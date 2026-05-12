package io.github.eggohito.neo_apoli.command;

import com.google.gson.JsonElement;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.command.argument.action.ActionKindArgument;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliActionKinds;
import io.github.eggohito.neo_apoli.util.JsonTextFormatter;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.RegistryOps;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ActionCommand {

	public static void register(CommandBuildContext registryAccess, CommandNode<CommandSourceStack> rootNode) {

		CommandNode<CommandSourceStack> baseNode = literal("action")
			.requires(source -> source.hasPermission(2))
			.build();

		baseNode.addChild(DumpSubCommand.node());
		baseNode.addChild(ExecuteSubCommand.node(registryAccess));

		rootNode.addChild(baseNode);

	}

	static final class DumpSubCommand {

		private static final SuggestionProvider<CommandSourceStack> ACTION_SUGGESTIONS = (context, builder) -> {
			Action.Kind<?> category = ActionKindArgument.getCategory(context, "category");
			return SharedSuggestionProvider.suggestResource(ActionManager.ids(category), builder);
		};

		static CommandNode<CommandSourceStack> node() {

			var node = literal("dump")
				.then(argument("category", ActionKindArgument.category())
					.then(argument("action", ResourceLocationArgument.id())
						.suggests(ACTION_SUGGESTIONS)
						.executes(DumpSubCommand::withDefaultIndent)
						.then(argument("indent", IntegerArgumentType.integer(0))
							.executes(DumpSubCommand::withSpecificIndent))));

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

			Action.Kind<?> category = ActionKindArgument.getCategory(commandContext, "category");
			Action action = ActionManager.getAsResult(category, ResourceLocationArgument.getId(commandContext, "action")).getOrThrow(error -> MiscUtil.createCommandException(() -> error));

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
			return NeoApoliActionKinds.addAsArguments(buildContext, literal("execute")).build();
		}

	}

}
