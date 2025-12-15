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
import io.github.eggohito.neo_apoli.duck.ContextBuilderHolder;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.JsonTextFormatter;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeySets;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ConditionCommand {

	public static void register(CommandBuildContext registryAccess, CommandNode<CommandSourceStack> rootNode) {

		CommandNode<CommandSourceStack> baseNode = literal("condition")
			.requires(source -> source.hasPermission(2))
			.build();

		baseNode.addChild(DumpSubCommand.node(registryAccess));
		baseNode.addChild(TestSubCommand.node(registryAccess));

		rootNode.addChild(baseNode);

	}

	static final class DumpSubCommand {

		static CommandNode<CommandSourceStack> node(CommandBuildContext registryAccess) {

			var node = literal("dump")
				.then(argument("condition", ConditionArgumentType.condition(registryAccess))
					.executes(DumpSubCommand::withDefaultIndent)
					.then(argument("indent", IntegerArgumentType.integer(0))
						.executes(DumpSubCommand::withSpecificIndent)));

			return node.build();

		}

		static int withDefaultIndent(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(commandContext, 4);
		}

		static int withSpecificIndent(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			return execute(commandContext, IntegerArgumentType.getInteger(commandContext, "indent"));
		}

		static int execute(CommandContext<CommandSourceStack> commandContext, int indent) throws CommandSyntaxException {

			CommandSourceStack commandSource = commandContext.getSource();
			RegistryOps<JsonElement> ops = commandSource.registryAccess().createSerializationContext(JsonOps.INSTANCE);

			Condition condition = ConditionArgumentType.getCondition(commandContext, "condition");

			return switch (Condition.CODEC.encodeStart(ops, condition)) {
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

	public static final class TestSubCommand {

		 static CommandNode<CommandSourceStack> node(CommandBuildContext registryAccess) {

			CommandNode<CommandSourceStack> baseNode = literal("test").build();
			CommandNode<CommandSourceStack> withNode = literal("with").build();
			CommandNode<CommandSourceStack> onNode = literal("on")
				.then(argument("condition", ConditionArgumentType.inlineCondition(registryAccess))
					.executes(TestSubCommand::testAsInt)).build();

			for (var parameter : NeoApoliRegistries.TYPED_CONTEXT_KEY) {

				String id = parameter.name().toString();
				TypedContextKey.CommandBuilder parameterCommandBuilder = parameter.getCommandBuilder();

				if (parameterCommandBuilder == null) {
					continue;
				}

				CommandNode<CommandSourceStack> parameterNode = literal(id).build();
				parameterCommandBuilder.addArguments(registryAccess, baseNode, parameterNode);

				withNode.addChild(parameterNode);

			}

			baseNode.addChild(withNode);
			baseNode.addChild(onNode);

			return baseNode;

		}

		 static int testAsInt(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

			if (test(commandContext)) {
				commandContext.getSource().sendSuccess(() -> Component.translatable("commands.execute.conditional.pass"), false);
				return 1;
			}

			else {
				throw MiscUtil.createCommandException(Component.translatable("commands.execute.conditional.fail"));
			}

		}

		public static boolean test(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

			CommandSourceStack source = commandContext.getSource();
			Context.Builder contextBuilder = ((ContextBuilderHolder) source).neo_apoli$getContextBuilder();

			Condition condition = ConditionArgumentType.getCondition(commandContext, "condition");
			String display = condition.asDisplayString(false);

			try {

				String rootPath = ConditionManager.getIdAsResult(condition).mapOrElse(
					id -> "{\"" + id + "\"}",
					error -> "{type: \"" + RegistryUtil.getId(NeoApoliRegistries.CONDITION_TYPE, condition.getType()) + "\", ...}"
				);

				ContextAware.ProblemReporter reporter = new ContextAware.ProblemReporter(NeoApoliContextKeySets.ANY, rootPath);
				Context context = contextBuilder
					.withReporter(reporter)
					.build(source.getLevel());

				condition.validate(reporter);

				if (reporter.hasAnyErrors()) {
					throw MiscUtil.createCommandException(Component.literal("Found errors when validating " + display + ": ").append(reporter.getErrorsAsString()));
				}

				boolean result = condition.test(context) && !reporter.hasAnyErrors();

				if (reporter.hasAnyErrors()) {
					source.sendSuccess(() -> Component.literal("").append("Warnings found when testing " + display + ": ").withStyle(ChatFormatting.YELLOW).append(reporter.getErrorsAsString()), false);
				}

				return result;

			}

			catch (Exception e) {
				throw MiscUtil.createCommandException(() -> "Error testing " + display + ": " + e.getMessage());
			}

		}

	}

}
