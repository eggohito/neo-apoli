package io.github.eggohito.neo_apoli.util.context.parameter.number;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.duck.ServerContextBuilderHolder;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextParameter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.server.command.CommandManager.argument;

public class FloatContextParameter extends TypedContextParameter<Float> {

	public FloatContextParameter(Identifier id) {
		super(id, Float.class);
	}

	@Override
	public @Nullable CommandBuilder getCommandBuilder() {
		return new CommandBuilder() {

			@Override
			public void addArguments(CommandRegistryAccess registryAccess, CommandNode<ServerCommandSource> executeNode, CommandNode<ServerCommandSource> parameterNode) {

				CommandNode<ServerCommandSource> valueNode = argument("value", FloatArgumentType.floatArg())
					.redirect(executeNode, this::redirect)
					.build();

				parameterNode.addChild(valueNode);

			}

			ServerCommandSource redirect(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

				ServerCommandSource source = context.getSource();
				float value = FloatArgumentType.getFloat(context, "value");

				((ServerContextBuilderHolder) source).neo_apoli$getBuilder().add(FloatContextParameter.this, value);
				return source;

			}

		};
	}

}
