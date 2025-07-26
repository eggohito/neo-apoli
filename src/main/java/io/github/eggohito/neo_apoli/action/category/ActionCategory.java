package io.github.eggohito.neo_apoli.action.category;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.util.category.Category;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class ActionCategory<A extends Action> implements Category<A> {

	@Nullable
	public Function<String, CommandBuilder> commandBuilderFactory() {
		return null;
	}

	@FunctionalInterface
	public interface CommandBuilder {
		ArgumentBuilder<ServerCommandSource, ?> addArguments(CommandRegistryAccess registryAccess, ArgumentBuilder<ServerCommandSource, ?> builder);
	}

}
