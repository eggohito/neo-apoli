package io.github.eggohito.neo_apoli.mixin.misc;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.command.ConditionCommand;
import io.github.eggohito.neo_apoli.command.argument.ConditionArgumentType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextParameter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ExecuteCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@Mixin(ExecuteCommand.class)
public abstract class ExecuteCommandMixin {

	@Shadow
	private static ArgumentBuilder<ServerCommandSource, ?> addConditionLogic(CommandNode<ServerCommandSource> root, ArgumentBuilder<ServerCommandSource, ?> builder, boolean positive, ExecuteCommand.Condition condition) {
		throw new AssertionError();
	}

	@ModifyReturnValue(method = "addConditionArguments", at = @At("RETURN"))
	private static ArgumentBuilder<ServerCommandSource, ?> addCustomConditionArgs(ArgumentBuilder<ServerCommandSource, ?> original, CommandNode<ServerCommandSource> rootNode, LiteralArgumentBuilder<ServerCommandSource> builder, boolean positive, CommandRegistryAccess registryAccess) {

		CommandNode<ServerCommandSource> baseNode = literal(NeoApoli.id("condition").toString()).build();
		CommandNode<ServerCommandSource> withNode = literal("with").build();
		CommandNode<ServerCommandSource> onNode = literal("on")
			.then(addConditionLogic(
				rootNode,
				argument("condition", ConditionArgumentType.inlineCondition(registryAccess)),
				positive,
				ConditionCommand.TestSubCommand::test
			)).build();

		for (var parameter : NeoApoliRegistries.TYPED_CONTEXT_PARAMETER) {

			String id = parameter.getId().toString();
			TypedContextParameter.CommandBuilder parameterCommandBuilder = parameter.getCommandBuilder();

			if (parameterCommandBuilder == null) {
				continue;
			}

			CommandNode<ServerCommandSource> parameterNode = literal(id).build();
			parameterCommandBuilder.addArguments(registryAccess, baseNode, parameterNode);

			withNode.addChild(parameterNode);

		}

		baseNode.addChild(withNode);
		baseNode.addChild(onNode);

		return builder.then(baseNode);

	}

}
