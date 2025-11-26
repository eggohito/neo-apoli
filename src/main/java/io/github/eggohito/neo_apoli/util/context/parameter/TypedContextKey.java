package io.github.eggohito.neo_apoli.util.context.parameter;

import com.mojang.brigadier.tree.CommandNode;
import lombok.Getter;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;
import org.jetbrains.annotations.Nullable;

@Getter
public class TypedContextKey<T> extends ContextKey<T> {

	private final Class<T> typeClass;

	public TypedContextKey(ResourceLocation id, Class<T> typeClass) {
		super(id);
		this.typeClass = typeClass;
	}

	@Nullable
	public TypedContextKey.CommandBuilder getCommandBuilder() {
		return null;
	}

	@FunctionalInterface
	public interface CommandBuilder {
		void addArguments(CommandBuildContext registryAccess, CommandNode<CommandSourceStack> baseNode, CommandNode<CommandSourceStack> parameterNode);
	}

}
