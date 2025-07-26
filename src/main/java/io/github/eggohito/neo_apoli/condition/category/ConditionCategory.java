package io.github.eggohito.neo_apoli.condition.category;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.mixin.access.ExecuteCommandAccessor;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.category.Category;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ExecuteCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public abstract class ConditionCategory<C extends Condition> implements Category<C> {

	@Nullable
	public Function<String, CommandBuilder> commandBuilderFactory() {
		return null;
	}

	@FunctionalInterface
	public interface CommandBuilder {

		ArgumentBuilder<ServerCommandSource, ?> addArguments(Optional<CommandNode<ServerCommandSource>> root, CommandRegistryAccess registryAccess, ArgumentBuilder<ServerCommandSource, ?> builder, boolean positive);

		static ArgumentBuilder<ServerCommandSource, ?> optionallyAddForkedConditionLogic(Optional<CommandNode<ServerCommandSource>> root, ArgumentBuilder<ServerCommandSource, ?> builder, boolean positive, ExecuteCommand.Condition condition) {

			if (root.isPresent()) {
				return ExecuteCommandAccessor.callAddConditionLogic(root.get(), builder, positive, condition);
			}

			else {
				return builder.executes(context -> {

					if (positive == condition.test(context)) {
						context.getSource().sendFeedback(() -> Text.translatable("commands.execute.conditional.pass"), false);
						return 1;
					}

					else {
						throw MiscUtil.createCommandException(Text.translatable("commands.execute.conditional.fail"));
					}

				});
			}

		}

	}

}
