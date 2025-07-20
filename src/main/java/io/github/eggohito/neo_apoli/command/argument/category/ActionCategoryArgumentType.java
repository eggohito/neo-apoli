package io.github.eggohito.neo_apoli.command.argument.category;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.eggohito.neo_apoli.action.category.ActionCategories;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.server.command.ServerCommandSource;

public class ActionCategoryArgumentType extends CategoryArgumentType<ActionCategory<?>> {

	public ActionCategoryArgumentType() {
		super(NeoApoliRegistries.ACTION_CATEGORY, ActionCategories.CODEC);
	}

	public static ActionCategoryArgumentType category() {
		return new ActionCategoryArgumentType();
	}

	public static ActionCategory<?> getCategory(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
		return context.getArgument(argumentName, ActionCategory.class);
	}

}
