package io.github.eggohito.neo_apoli.command.argument.category;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.server.command.ServerCommandSource;

public class ConditionCategoryArgumentType extends CategoryArgumentType<ConditionCategory<?>> {

	public ConditionCategoryArgumentType() {
		super(NeoApoliRegistries.CONDITION_CATEGORY, ConditionCategory.CODEC);
	}

	public static ConditionCategoryArgumentType category() {
		return new ConditionCategoryArgumentType();
	}

	public static ConditionCategory<?> getCategory(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
		return context.getArgument(argumentName, ConditionCategory.class);
	}

}
