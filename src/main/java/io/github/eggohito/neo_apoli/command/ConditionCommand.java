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
import io.github.eggohito.neo_apoli.condition.ConditionManager;
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

			return switch (Condition.CODEC.encodeStart(ops, condition)) {
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

	public static final class TestSubCommand {

		 static CommandNode<ServerCommandSource> node(CommandRegistryAccess registryAccess) {

			CommandNode<ServerCommandSource> baseNode = literal("test").build();
			CommandNode<ServerCommandSource> withNode = literal("with").build();
			CommandNode<ServerCommandSource> onNode = literal("on")
				.then(argument("condition", ConditionArgumentType.inlineCondition(registryAccess))
					.executes(TestSubCommand::testAsInt)).build();

			for (var parameter : NeoApoliRegistries.TYPED_CONTEXT_PARAMETER) {

				String id = parameter.getId().toString();
				TypedContextParameter.CommandBuilder parameterCommandBuilder = parameter.getCommandBuilder();

				if (parameterCommandBuilder == null) {
					continue;
				}

				CommandNode<ServerCommandSource> parameterNode = literal(id).build();
				parameterCommandBuilder.addArguments(registryAccess, baseNode, parameterNode);

				withNode.addChild(parameterNode);

			}

			baseNode.addChild(withNode);
			baseNode.addChild(onNode);

			return baseNode;

		}

		 static int testAsInt(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {

			if (test(commandContext)) {
				commandContext.getSource().sendFeedback(() -> Text.translatable("commands.execute.conditional.pass"), false);
				return 1;
			}

			else {
				throw MiscUtil.createCommandException(Text.translatable("commands.execute.conditional.fail"));
			}

		}

		public static boolean test(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {

			ServerCommandSource source = commandContext.getSource();
			ServerContext.Builder builder = ((ServerContextBuilderHolder) source).neo_apoli$getBuilder();

			Condition condition = ConditionArgumentType.getCondition(commandContext, "condition");
			String display = condition.asDisplayString(false);

			try {

				String rootPath = ConditionManager.getIdAsResult(condition).mapOrElse(
					id -> "{\"" + id + "\"}",
					error -> "{type: \"" + RegistryUtil.getId(NeoApoliRegistries.CONDITION_TYPE, condition.getType()) + "\", ...}"
				);

				ContextAware.ErrorReporter reporter = new ContextAware.ErrorReporter(NeoApoliContextTypes.ANY, rootPath);
				ServerContext serverContext = builder
					.withReporter(reporter)
					.build(source.getWorld());

				condition.validate(reporter);

				if (reporter.hasAnyErrors()) {
					throw MiscUtil.createCommandException(Text.literal("Found errors when validating " + display + ": ").append(reporter.getErrorsAsString()));
				}

				boolean result = condition.test(serverContext) && !reporter.hasAnyErrors();

				if (reporter.hasAnyErrors()) {
					source.sendFeedback(() -> Text.literal("").append("Warnings found when testing " + display + ": ").formatted(Formatting.YELLOW).append(reporter.getErrorsAsString()), false);
				}

				return result;

			}

			catch (Exception e) {
				throw MiscUtil.createCommandException(() -> "Error testing " + display + ": " + e.getMessage());
			}

		}

	}

}
