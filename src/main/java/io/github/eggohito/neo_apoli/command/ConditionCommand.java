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
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextBuilderHolder;
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
				.then(argument("condition", ConditionArgument.condition(registryAccess))
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

			Condition condition = ConditionArgument.getCondition(commandContext, "condition");

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

		 static CommandNode<CommandSourceStack> node(CommandBuildContext buildContext) {

			 var baseNode = literal("test").build();
			 var withNode = literal("with").build();
			 var conditionNode = argument("condition", ConditionArgument.inlineCondition(buildContext)).build();

			 NeoApoliContextParams.addAllAsArguments(buildContext, baseNode, withNode, conditionNode);
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
			Context.Builder builder = ((ContextBuilderHolder) source).neo_apoli$getContextBuilder();

			Condition condition = ConditionArgument.getCondition(commandContext, "action");
			String path = ConditionManager.getIdAsResult(condition).mapOrElse(id -> "{\"" + id + "\"}", error -> "{type: \"" + Util.getRegisteredName(NeoApoliRegistries.CONDITION_TYPE, condition.getType()) + "\"");

			Reporter reporter = new Reporter(path);
			Context.Validator validator = new Context.Validator(LootContextParamSets.EMPTY, reporter);

			condition.validate(validator);
			var validationException = reporter.getErrorsFlattened()
				.map(error -> Component.literal("Found errors while validating condition: ").append(error))
				.map(MiscUtil::createCommandException);

			if (validationException.isPresent()) {
				throw validationException.get();
			}

			Context context = builder
				.withReporter(reporter)
				.build(source.getLevel());

			boolean result = condition.test(context);
			var executionException = reporter.getErrorsFlattened()
				.map(error -> Component.literal("Found errors while executing condition: ").append(error))
				.map(MiscUtil::createCommandException);

			if (executionException.isPresent()) {
				throw executionException.get();
			}

			source.sendSuccess(() -> Component.nullToEmpty("Successfully executed action!"), true);
			return result;

		}

	}

}
