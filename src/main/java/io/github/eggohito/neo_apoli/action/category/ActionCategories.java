package io.github.eggohito.neo_apoli.action.category;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.command.argument.ActionArgumentType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.ServerCommandSource;

import java.util.function.Consumer;
import java.util.function.Function;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class ActionCategories {

	public static final BiEntityActionCategory BIENTITY_ACTION = register(new BiEntityActionCategory());
	public static final BlockActionCategory BLOCK_ACTION = register(new BlockActionCategory());
	public static final EntityActionCategory ENTITY_ACTION = register(new EntityActionCategory());
	public static final ItemActionCategory ITEM_ACTION = register(new ItemActionCategory());

	public static void registerAll() {

	}

	public static <A extends Action, C extends ActionCategory<A>> C register(C category) {
		return Registry.register(NeoApoliRegistries.ACTION_CATEGORY, category.registryRef().getValue(), category);
	}

	public static ArgumentBuilder<ServerCommandSource, ?> addArguments(CommandRegistryAccess registryAccess, ArgumentBuilder<ServerCommandSource, ?> builder) {

		for (var category : NeoApoliRegistries.ACTION_CATEGORY) {

			String categoryId = category.registryRef().getValue().toString();
			Function<String, ActionCategory.CommandBuilder> commandBuilderFactory = category.commandBuilderFactory();

			if (commandBuilderFactory == null) {
				continue;
			}

			Consumer<String> finalizer = key -> builder
				.then(literal(categoryId)
					.then(commandBuilderFactory.apply(key).addArguments(registryAccess, argument(key, ActionArgumentType.action(registryAccess, category)))));

			finalizer.accept("action");

		}

		return builder;

	}

}
