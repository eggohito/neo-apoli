package io.github.eggohito.neo_apoli.command;

import com.google.gson.JsonElement;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.command.argument.category.ConditionCategoryArgumentType;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategories;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import io.github.eggohito.neo_apoli.util.JsonTextFormatter;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ConditionCommand {

	public static final CommandNode<ServerCommandSource> NODE = literal("condition")
		.requires(source -> source.hasPermissionLevel(2))
		.build();

	public static void register(CommandRegistryAccess registryAccess, CommandNode<ServerCommandSource> baseNode) {

		NODE.addChild(DumpSubCommand.node());
		NODE.addChild(TestSubCommand.node(registryAccess));

		baseNode.addChild(NODE);

	}

	static final class DumpSubCommand {

		static CommandNode<ServerCommandSource> node() {
			return literal("dump")
				.then(argument("category", ConditionCategoryArgumentType.category())
					.then(argument("condition", IdentifierArgumentType.identifier())
						.suggests((context, builder) -> CommandSource.suggestIdentifiers(ConditionManager.streamIds(ConditionCategoryArgumentType.getCategory(context, "category")), builder))
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

			ConditionCategory<Condition> category = (ConditionCategory<Condition>) ConditionCategoryArgumentType.getCategory(commandContext, "category");
			Condition condition = ConditionManager.getAsResult(category, IdentifierArgumentType.getIdentifier(commandContext, "condition")).getOrThrow(err -> MiscUtil.createCommandException(() -> err));

			ServerCommandSource commandSource = commandContext.getSource();
			RegistryOps<JsonElement> jsonOps = commandSource.getRegistryManager().getOps(JsonOps.INSTANCE);

			return switch (category.baseCodec().encodeStart(jsonOps, condition)) {
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

	static final class TestSubCommand {

		static CommandNode<ServerCommandSource> node(CommandRegistryAccess registryAccess) {
			return ConditionCategories.addArguments(NODE, registryAccess, literal("test"), true).build();
		}

	}

}
