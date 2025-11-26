package io.github.eggohito.neo_apoli.util.context.parameter.entity;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.duck.ServerContextBuilderHolder;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.commands.Commands.argument;

public class EntityContextKey extends TypedContextKey<Entity> {

	public EntityContextKey(ResourceLocation id) {
		super(id, Entity.class);
	}

	@Override
	public @Nullable TypedContextKey.CommandBuilder getCommandBuilder() {
		return new CommandBuilder() {

			@Override
			public void addArguments(CommandBuildContext registryAccess, CommandNode<CommandSourceStack> baseNode, CommandNode<CommandSourceStack> parameterNode) {

				CommandNode<CommandSourceStack> entityNode = argument("entity", EntityArgument.entity())
					.redirect(baseNode, this::redirect)
					.build();

				parameterNode.addChild(entityNode);

			}

			CommandSourceStack redirect(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

				CommandSourceStack source = context.getSource();
				Entity entity = EntityArgument.getEntity(context, "entity");

				((ServerContextBuilderHolder) source).neo_apoli$getBuilder().add(EntityContextKey.this, entity);
				return source;

			}

		};
	}

}
