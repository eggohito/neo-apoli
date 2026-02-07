package io.github.eggohito.neo_apoli.context.parameter;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.context.ContextBuilderHolder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.commands.Commands.argument;

public class Vec3ContextParameter extends ContextParameter<Vec3> {

	public Vec3ContextParameter(ResourceLocation name) {
		super(name);
	}

	@Override
	public @NotNull Class<Vec3> typeClass() {
		return Vec3.class;
	}

	@Override
	public void addAsArgument(CommandBuildContext buildContext, CommandNode<CommandSourceStack> baseNode, CommandNode<CommandSourceStack> parameterNode) {

		CommandNode<CommandSourceStack> vecNode = argument("vec", Vec3Argument.vec3())
			.redirect(baseNode, this::addVecToSource)
			.build();

		parameterNode.addChild(vecNode);

	}

	protected CommandSourceStack addVecToSource(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

		CommandSourceStack source = context.getSource();
		Vec3 vec = Vec3Argument.getVec3(context, "vec");

		((ContextBuilderHolder) source).neo_apoli$getContextBuilder().withRequired(this, vec);
		return source;

	}

}
