package io.github.eggohito.neo_apoli.util.context.parameter;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.duck.ContextBuilderHolder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.commands.Commands.argument;

public class Vec3ContextKey extends TypedContextKey<Vec3> {

	public Vec3ContextKey(ResourceLocation id) {
		super(id, Vec3.class);
	}

	@Override
	public @Nullable CommandBuilder getCommandBuilder() {
		return new CommandBuilder() {

			@Override
			public void addArguments(CommandBuildContext registryAccess, CommandNode<CommandSourceStack> baseNode, CommandNode<CommandSourceStack> parameterNode) {

				CommandNode<CommandSourceStack> posNode = argument("pos", Vec3Argument.vec3())
					.redirect(baseNode, this::redirect)
					.build();

				parameterNode.addChild(posNode);

			}

			CommandSourceStack redirect(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

				CommandSourceStack source = context.getSource();
				Vec3 pos = Vec3Argument.getVec3(context, "pos");

				((ContextBuilderHolder) source).neo_apoli$getContextBuilder().add(Vec3ContextKey.this, pos);
				return source;

			}

		};
	}

}
