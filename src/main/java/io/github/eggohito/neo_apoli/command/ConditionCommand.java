package io.github.eggohito.neo_apoli.command;

import com.google.gson.JsonElement;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.command.argument.condition.ConditionKindArgument;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliConditionKinds;
import io.github.eggohito.neo_apoli.util.JsonTextFormatter;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.RegistryOps;

import java.util.Optional;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ConditionCommand {

	public static void register(CommandBuildContext registryAccess, CommandNode<CommandSourceStack> rootNode) {

		CommandNode<CommandSourceStack> baseNode = literal("condition")
			.requires(source -> source.hasPermission(2))
			.build();

		baseNode.addChild(DumpSubCommand.node());
		baseNode.addChild(TestSubCommand.node(registryAccess));

		rootNode.addChild(baseNode);

	}

	static final class DumpSubCommand {

		private static final SuggestionProvider<CommandSourceStack> CONDITION_SUGGESTIONS = (context, builder) -> {
			Condition.Kind<?> category = ConditionKindArgument.getCategory(context, "category");
			return SharedSuggestionProvider.suggestResource(ConditionManager.ids(category), builder);
		};

		static CommandNode<CommandSourceStack> node() {

			var node = literal("dump")
				.then(argument("category", ConditionKindArgument.category())
					.then(argument("condition", ResourceLocationArgument.id())
						.suggests(CONDITION_SUGGESTIONS)
						.executes(DumpSubCommand::withDefaultIndent)
						.then(argument("indent", IntegerArgumentType.integer(0))
							.executes(DumpSubCommand::withSpecificIndent))));

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

			Condition.Kind<?> category = ConditionKindArgument.getCategory(commandContext, "category");
			Condition condition = ConditionManager.getAsResult(category, ResourceLocationArgument.getId(commandContext, "condition")).getOrThrow(error -> MiscUtil.createCommandException(() -> error));

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
			 return NeoApoliConditionKinds.addAsArguments(Optional.empty(), buildContext, literal("test"), true).build();
		}

	}

}
