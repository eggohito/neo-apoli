package io.github.eggohito.neo_apoli.mixin.misc;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.command.ConditionCommand;
import io.github.eggohito.neo_apoli.command.argument.ConditionArgumentType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.ExecuteCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@Mixin(ExecuteCommand.class)
public abstract class ExecuteCommandMixin {

	@Shadow
	private static ArgumentBuilder<CommandSourceStack, ?> addConditional(CommandNode<CommandSourceStack> root, ArgumentBuilder<CommandSourceStack, ?> builder, boolean positive, ExecuteCommand.CommandPredicate condition) {
		throw new AssertionError();
	}

	@ModifyReturnValue(method = "addConditionals", at = @At("RETURN"))
	private static ArgumentBuilder<CommandSourceStack, ?> addCustomConditionals(ArgumentBuilder<CommandSourceStack, ?> original, CommandNode<CommandSourceStack> rootNode, LiteralArgumentBuilder<CommandSourceStack> builder, boolean positive, CommandBuildContext registryAccess) {

		CommandNode<CommandSourceStack> baseNode = literal(NeoApoli.id("condition").toString()).build();
		CommandNode<CommandSourceStack> withNode = literal("with").build();
		CommandNode<CommandSourceStack> onNode = literal("on")
			.then(addConditional(
				rootNode,
				argument("condition", ConditionArgumentType.inlineCondition(registryAccess)),
				positive,
				ConditionCommand.TestSubCommand::test
			)).build();

		for (var parameter : NeoApoliRegistries.TYPED_CONTEXT_KEY) {

			String id = parameter.name().toString();
			TypedContextKey.CommandBuilder parameterCommandBuilder = parameter.getCommandBuilder();

			if (parameterCommandBuilder == null) {
				continue;
			}

			CommandNode<CommandSourceStack> parameterNode = literal(id).build();
			parameterCommandBuilder.addArguments(registryAccess, baseNode, parameterNode);

			withNode.addChild(parameterNode);

		}

		baseNode.addChild(withNode);
		baseNode.addChild(onNode);

		return builder.then(baseNode);

	}

}
