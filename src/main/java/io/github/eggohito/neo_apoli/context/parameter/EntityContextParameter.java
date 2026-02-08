package io.github.eggohito.neo_apoli.context.parameter;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.commands.Commands.argument;

public class EntityContextParameter extends ContextParameter<Entity> {

	public EntityContextParameter(ResourceLocation name) {
		super(name);
	}

	@Override
	public @NotNull Class<Entity> typeClass() {
		return Entity.class;
	}

	@Override
	public void addAsArgument(CommandBuildContext buildContext, CommandNode<CommandSourceStack> baseNode, CommandNode<CommandSourceStack> parameterNode) {

		CommandNode<CommandSourceStack> entityNode = argument("entity", EntityArgument.entity())
			.redirect(baseNode, this::addEntityToContext)
			.build();

		parameterNode.addChild(entityNode);

	}

	protected CommandSourceStack addEntityToContext(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

		CommandSourceStack source = context.getSource();
		Entity entity = EntityArgument.getEntity(context, "entity");

		source.neo_apoli$getContextBuilder().withRequired(this, entity);
		return source;

	}

}
