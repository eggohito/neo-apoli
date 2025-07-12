package io.github.eggohito.neo_apoli.mixin.misc;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategories;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ExecuteCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(ExecuteCommand.class)
public abstract class ExecuteCommandMixin {

	@ModifyReturnValue(method = "addConditionArguments", at = @At("RETURN"))
	private static ArgumentBuilder<ServerCommandSource, ?> addCustomConditionArgs(ArgumentBuilder<ServerCommandSource, ?> original, CommandNode<ServerCommandSource> root, LiteralArgumentBuilder<ServerCommandSource> builder, boolean positive, CommandRegistryAccess registryAccess) {
		return original.then(ConditionCategories.addArguments(Optional.of(root), registryAccess, CommandManager.literal(NeoApoli.id("condition").toString()), positive));
	}

}
