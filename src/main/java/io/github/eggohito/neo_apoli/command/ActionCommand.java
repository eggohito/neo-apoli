package io.github.eggohito.neo_apoli.command;

import com.google.gson.JsonElement;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.command.argument.ActionArgumentType;
import io.github.eggohito.neo_apoli.command.argument.category.ActionCategoryArgumentType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.JsonTextFormatter;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.function.Function;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ActionCommand {

	public static final CommandNode<ServerCommandSource> NODE = literal("action")
		.requires(source -> source.hasPermissionLevel(2))
		.build();

	public static void register(CommandRegistryAccess registryAccess, CommandNode<ServerCommandSource> baseNode) {

		NODE.addChild(DumpSubCommand.node());
		NODE.addChild(ExecuteSubCommand.node(registryAccess));

		baseNode.addChild(NODE);

	}

	static final class DumpSubCommand {

		static CommandNode<ServerCommandSource> node() {
			return literal("dump")
				.then(argument("category", ActionCategoryArgumentType.category())
					.then(argument("action", IdentifierArgumentType.identifier())
						.suggests((context, builder) -> CommandSource.suggestIdentifiers(ActionManager.streamIds(ActionCategoryArgumentType.getCategory(context, "category")), builder))
						.executes(DumpSubCommand::withDefaultIndent)
						.then(argument("indent", IntegerArgumentType.integer(0))
							.executes(DumpSubCommand::withSpecificIndent)))).build();
		}

		static int withDefaultIndent(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
			return execute(commandContext, 4);
		}

		static int withSpecificIndent(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
			return execute(commandContext, IntegerArgumentType.getInteger(commandContext, "indent"));
		}

		@SuppressWarnings("unchecked")
		static int execute(CommandContext<ServerCommandSource> commandContext, int indent) throws CommandSyntaxException {

			ActionCategory<Action> category = (ActionCategory<Action>) ActionCategoryArgumentType.getCategory(commandContext, "category");
			Action action = ActionManager.getAsResult(category, IdentifierArgumentType.getIdentifier(commandContext, "action")).getOrThrow(err -> MiscUtil.createCommandException(() -> err));

			ServerCommandSource commandSource = commandContext.getSource();
			RegistryOps<JsonElement> jsonOps = commandSource.getRegistryManager().getOps(JsonOps.INSTANCE);

			return switch (category.baseCodec().encodeStart(jsonOps, action)) {
				case DataResult.Success<JsonElement> success -> {
					commandSource.sendFeedback(() -> JsonTextFormatter.format(success.value(), indent), false);
					yield 1;
				}
				case DataResult.Error<JsonElement> error -> {
					commandSource.sendError(Text.literal(error.message()));
					yield 0;
				}
			};

		}

	}

	static final class ExecuteSubCommand {

		static CommandNode<ServerCommandSource> node(CommandRegistryAccess registryAccess) {

			LiteralArgumentBuilder<ServerCommandSource> builder = literal("execute");
			for (var actionCategory : NeoApoliRegistries.ACTION_CATEGORY) {

				String categoryId = actionCategory.registryRef().getValue().toString();
				Function<String, ActionCategory.CommandBuilder> commandBuilderFactory = actionCategory.commandBuilderFactory();

				if (commandBuilderFactory == null) {
					continue;
				}

				Function<String, ArgumentBuilder<ServerCommandSource, ?>> finalizer = key -> builder
					.then(literal(categoryId)
						.then(commandBuilderFactory.apply(key).addArguments(registryAccess, argument(key, ActionArgumentType.action(registryAccess, actionCategory)))));

				finalizer.apply("action");

			}

			return builder.build();

		}

	}

}
