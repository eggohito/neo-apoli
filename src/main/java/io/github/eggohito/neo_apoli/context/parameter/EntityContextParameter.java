package io.github.eggohito.neo_apoli.context.parameter;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.context.Context;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public final class EntityContextParameter extends Context.Parameter<Entity> {

	public EntityContextParameter(ResourceLocation name) {
		super(name);
	}

	@Override
	public @NotNull Class<Entity> getTypeClass() {
		return Entity.class;
	}

	@Override
	public void addAsArgument(CommandBuildContext buildContext, CommandNode<CommandSourceStack> baseNode, CommandNode<CommandSourceStack> parameterNode) {

		var entityNode = Commands.argument("entity", EntityArgument.entity())
			.redirect(baseNode, this::addToSource)
			.build();

		parameterNode.addChild(entityNode);

	}

	CommandSourceStack addToSource(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

		CommandSourceStack source = context.getSource();
		source.neo_apoli$getContextBuilder().withRequired(this, EntityArgument.getEntity(context, "entity"));

		return source;

	}

}
