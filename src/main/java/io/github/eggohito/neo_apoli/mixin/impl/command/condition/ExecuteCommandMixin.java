package io.github.eggohito.neo_apoli.mixin.impl.command.condition;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliConditionKinds;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.commands.ExecuteCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(ExecuteCommand.class)
public abstract class ExecuteCommandMixin {

	@ModifyReturnValue(method = "addConditionals", at = @At("RETURN"))
	private static ArgumentBuilder<CommandSourceStack, ?> addCustomConditionals(ArgumentBuilder<CommandSourceStack, ?> original, CommandNode<CommandSourceStack> rootNode, LiteralArgumentBuilder<CommandSourceStack> builder, boolean positive, CommandBuildContext buildContext) {
		return original.then(NeoApoliConditionKinds.addAsArguments(Optional.of(rootNode), buildContext, Commands.literal(NeoApoli.id("condition").toString()), positive));
	}

}
