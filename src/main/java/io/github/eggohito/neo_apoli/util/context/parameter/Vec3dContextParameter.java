package io.github.eggohito.neo_apoli.util.context.parameter;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.duck.ServerContextBuilderHolder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.server.command.CommandManager.argument;

public class Vec3dContextParameter extends TypedContextParameter<Vec3d> {

	public Vec3dContextParameter(Identifier id) {
		super(id, Vec3d.class);
	}

	@Override
	public @Nullable CommandBuilder getCommandBuilder() {
		return new CommandBuilder() {

			@Override
			public void addArguments(CommandRegistryAccess registryAccess, CommandNode<ServerCommandSource> baseNode, CommandNode<ServerCommandSource> parameterNode) {

				CommandNode<ServerCommandSource> posNode = argument("pos", Vec3ArgumentType.vec3())
					.redirect(baseNode, this::redirect)
					.build();

				parameterNode.addChild(posNode);

			}

			ServerCommandSource redirect(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

				ServerCommandSource source = context.getSource();
				Vec3d pos = Vec3ArgumentType.getVec3(context, "pos");

				((ServerContextBuilderHolder) source).neo_apoli$getBuilder().add(Vec3dContextParameter.this, pos);
				return source;

			}

		};
	}

}
