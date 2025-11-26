package io.github.eggohito.neo_apoli.util.context.parameter.number;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.duck.ServerContextBuilderHolder;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.commands.Commands.argument;

public class FloatContextKey extends TypedContextKey<Float> {

	public FloatContextKey(ResourceLocation id) {
		super(id, Float.class);
	}

	@Override
	public @Nullable CommandBuilder getCommandBuilder() {
		return new CommandBuilder() {

			@Override
			public void addArguments(CommandBuildContext registryAccess, CommandNode<CommandSourceStack> baseNode, CommandNode<CommandSourceStack> parameterNode) {

				CommandNode<CommandSourceStack> valueNode = argument("value", FloatArgumentType.floatArg())
					.redirect(baseNode, this::redirect)
					.build();

				parameterNode.addChild(valueNode);

			}

			CommandSourceStack redirect(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

				CommandSourceStack source = context.getSource();
				float value = FloatArgumentType.getFloat(context, "value");

				((ServerContextBuilderHolder) source).neo_apoli$getBuilder().add(FloatContextKey.this, value);
				return source;

			}

		};
	}

}
