package io.github.eggohito.neo_apoli.util.context.parameter;

import com.mojang.brigadier.tree.CommandNode;
import lombok.Getter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameter;
import org.jetbrains.annotations.Nullable;

@Getter
public class TypedContextParameter<T> extends ContextParameter<T> {

	private final Class<T> typeClass;

	public TypedContextParameter(Identifier id, Class<T> typeClass) {
		super(id);
		this.typeClass = typeClass;
	}

	@Nullable
	public TypedContextParameter.CommandBuilder getCommandBuilder() {
		return null;
	}

	@FunctionalInterface
	public interface CommandBuilder {
		void addArguments(CommandRegistryAccess registryAccess, CommandNode<ServerCommandSource> baseNode, CommandNode<ServerCommandSource> parameterNode);
	}

}
