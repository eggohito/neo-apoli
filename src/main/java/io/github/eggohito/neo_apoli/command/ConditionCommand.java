package io.github.eggohito.neo_apoli.command;

import com.google.gson.JsonElement;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.command.argument.ConditionArgument;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.manager.ConditionManager;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.JsonTextFormatter;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.Reporter;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ConditionCommand {

	public static void register(CommandBuildContext buildContext, CommandNode<CommandSourceStack> rootNode) {

		var baseNode = literal("condition")
			.requires(source -> source.hasPermission(2))
			.build();

		baseNode.addChild(Dump.node(buildContext));
		baseNode.addChild(Test.node(buildContext));

		rootNode.addChild(baseNode);

	}

	public static final class Dump {

		public static CommandNode<CommandSourceStack> node(CommandBuildContext buildContext) {

			var node = literal("dump")
				.then(argument("condition", ConditionArgument.condition(buildContext))
					.executes(Dump::withDefaultIndent)
					.then(argument("indent", IntegerArgumentType.integer(0))
						.executes(Dump::withSpecificIndent)));

			return node.build();

		}

		public static int withDefaultIndent(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
			return execute(context, 4);
		}

		public static int withSpecificIndent(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
			return execute(context, IntegerArgumentType.getInteger(context, "indent"));
		}

		public static int execute(CommandContext<CommandSourceStack> context, int indent) throws CommandSyntaxException {

			CommandSourceStack source = context.getSource();
			Condition condition = ConditionArgument.getCondition(context, "condition");

			return switch (Condition.CODEC.encodeStart(source.registryAccess().createSerializationContext(JsonOps.INSTANCE), condition)) {
				case DataResult.Success<JsonElement> success -> {

					JsonElement jsonElement = success.value();
					source.sendSuccess(() -> JsonTextFormatter.format(jsonElement, indent), false);

					yield jsonElement.toString().length();

				}
				case DataResult.Error<JsonElement> error ->
					throw MiscUtil.createCommandException(error::message);
			};

		}

	}

	public static final class Test {

		public static CommandNode<CommandSourceStack> node(CommandBuildContext buildContext) {

			var testNode = literal("test").build();
			var withNode = literal("with").build();
			var forNode = literal("for").build();
			var conditionNode = argument("condition", ConditionArgument.inlineCondition(buildContext)).executes(Test::testAsInt).build();

			NeoApoliContextParams.addAsArguments(buildContext, testNode, withNode);

			forNode.addChild(conditionNode);
			testNode.addChild(withNode);
			testNode.addChild(forNode);

			return testNode;

		}

		public static int testAsInt(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

			if (test(commandContext)) {
				commandContext.getSource().sendSuccess(() -> Component.translatable("commands.execute.conditional.pass"), false);
			}

			else {
				throw MiscUtil.createCommandException(Component.translatable("commands.execute.conditional.fail"));
			}

			return 1;

		}

		public static boolean test(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

			CommandSourceStack source = commandContext.getSource();
			Context.Builder contextBuilder = source.neo_apoli$getContextBuilder();

			Condition condition = ConditionArgument.getCondition(commandContext, "condition");
			String path = ConditionManager.INSTANCE.getKeyAsResult(condition).mapOrElse(id -> "{\"" + id + "\"}", error -> "{type: \"" + Util.getRegisteredName(NeoApoliRegistries.CONDITION_TYPE, condition.getType()) + "\"}");

			Reporter reporter = new Reporter(path);
			Context.Validator validator = new Context.Validator(contextBuilder.toKeySet(), reporter).withResolver(source.registryAccess());

			condition.validate(validator);

			var validationException = validator.reporter().getErrorsFlattened()
				.map(error -> Component.literal("Found errors while validating condition ").append(error))
				.map(MiscUtil::createCommandException);

			if (validationException.isPresent()) {
				throw validationException.get();
			}

			Context context = contextBuilder.withReporter(reporter).build(source.getLevel());
			boolean result = condition.test(context);

			var testException = context.reporter().getErrorsFlattened()
				.map(error -> Component.literal("Found errors while testing condition ").append(error))
				.map(MiscUtil::createCommandException);

			if (testException.isPresent()) {
				throw testException.get();
			}

			return result;

		}

	}

}
