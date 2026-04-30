package io.github.eggohito.neo_apoli.mixin.impl.command.condition;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.command.ConditionCommand;
import io.github.eggohito.neo_apoli.command.argument.ConditionArgument;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
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
	private static ArgumentBuilder<CommandSourceStack, ?> addCustomConditionals(ArgumentBuilder<CommandSourceStack, ?> original, CommandNode<CommandSourceStack> rootNode, LiteralArgumentBuilder<CommandSourceStack> builder, boolean positive, CommandBuildContext buildContext) {

		var baseNode = literal(NeoApoli.id("condition").toString()).build();
		var withNode = literal("with").build();
		var forNode = literal("for").build();
		var conditionNode = addConditional(rootNode, argument("condition", ConditionArgument.inlineCondition(buildContext)), positive, ConditionCommand.TestSubCommand::test).build();

		NeoApoliContextParams.addAllAsArguments(buildContext, baseNode, withNode);

		forNode.addChild(conditionNode);
		baseNode.addChild(withNode);
		baseNode.addChild(forNode);

		return builder.then(baseNode);

	}

}
