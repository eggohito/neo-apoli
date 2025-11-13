package io.github.eggohito.neo_apoli.mixin.misc;

import net.minecraft.server.command.ExecuteCommand;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ExecuteCommand.class)
public abstract class ExecuteCommandMixin {

	//	TODO: Implement dynamic command arguments for testing conditions
//	@ModifyReturnValue(method = "addConditionArguments", at = @At("RETURN"))
//	private static ArgumentBuilder<ServerCommandSource, ?> addCustomConditionArgs(ArgumentBuilder<ServerCommandSource, ?> original, CommandNode<ServerCommandSource> root, LiteralArgumentBuilder<ServerCommandSource> builder, boolean positive, CommandRegistryAccess registryAccess) {
//		return original.then(ConditionCategories.addArguments(Optional.of(root), registryAccess, CommandManager.literal(NeoApoli.id("condition").toString()), positive));
//	}

}
