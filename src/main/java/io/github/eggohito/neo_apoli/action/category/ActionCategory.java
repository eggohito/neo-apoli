package io.github.eggohito.neo_apoli.action.category;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.codec.ValueSuppliedElementCodec;
import io.github.eggohito.neo_apoli.util.category.Category;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class ActionCategory<A extends Action> implements Category<A> {

	private final Codec<A> entryCodec = new ValueSuppliedElementCodec<>(this.baseCodec(), true, id -> ActionManager.getAsResult(ActionCategory.this, id), ActionManager::getIdAsResult);

	@Override
	public Codec<A> entryCodec() {
		return entryCodec;
	}

	@Nullable
	public Function<String, CommandBuilder> commandBuilderFactory() {
		return null;
	}

	@FunctionalInterface
	public interface CommandBuilder {
		ArgumentBuilder<ServerCommandSource, ?> addArguments(CommandRegistryAccess registryAccess, ArgumentBuilder<ServerCommandSource, ?> builder);
	}

}
