package io.github.eggohito.neo_apoli.util.context.parameter.entity;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.duck.ServerContextBuilderHolder;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextParameter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.server.command.CommandManager.argument;

public class EntityContextParameter extends TypedContextParameter<Entity> {

	public EntityContextParameter(Identifier id) {
		super(id, Entity.class);
	}

	@Override
	public @Nullable TypedContextParameter.CommandBuilder getCommandBuilder() {
		return new CommandBuilder() {

			@Override
			public void addArguments(CommandRegistryAccess registryAccess, CommandNode<ServerCommandSource> baseNode, CommandNode<ServerCommandSource> parameterNode) {

				CommandNode<ServerCommandSource> entityNode = argument("entity", EntityArgumentType.entity())
					.redirect(baseNode, this::redirect)
					.build();

				parameterNode.addChild(entityNode);

			}

			ServerCommandSource redirect(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

				ServerCommandSource source = context.getSource();
				Entity entity = EntityArgumentType.getEntity(context, "entity");

				((ServerContextBuilderHolder) source).neo_apoli$getBuilder().add(EntityContextParameter.this, entity);
				return source;

			}

		};
	}

}
