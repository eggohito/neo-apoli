package io.github.eggohito.neo_apoli.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.PowerReference;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class PowerReferenceArgumentType implements ArgumentType<PowerReference> {

	private PowerReferenceArgumentType() {

	}

	@Override
	public PowerReference parse(StringReader reader) throws CommandSyntaxException {
		return PowerReference.parse(reader);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestMatching(PowerManager.streamReferences().map(PowerReference::toString), builder);
	}

	public static PowerReferenceArgumentType powerReference() {
		return new PowerReferenceArgumentType();
	}

	public static PowerReference getPowerReference(CommandContext<ServerCommandSource> context, String argumentName) {
		return context.getArgument(argumentName, PowerReference.class);
	}
	
	public static PowerReference getExistingPowerReference(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
		PowerReference reference = getPowerReference(context, argumentName);
		return PowerManager.getAsResult(reference)
			.map(power -> reference)
			.getOrThrow(err -> MiscUtil.createCommandException(() -> err));
	}

}
