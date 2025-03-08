package io.github.eggohito.neo_apoli.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.PowerIdentifier;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class PowerIdentifierArgumentType implements ArgumentType<PowerIdentifier> {

	private PowerIdentifierArgumentType() {

	}

	@Override
	public PowerIdentifier parse(StringReader reader) throws CommandSyntaxException {
		return PowerIdentifier.parse(reader);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestMatching(PowerManager.getIds().stream().map(PowerIdentifier::toString), builder);
	}

	public static PowerIdentifierArgumentType powerId() {
		return new PowerIdentifierArgumentType();
	}

	public static PowerIdentifier getPowerId(CommandContext<ServerCommandSource> context, String argumentName) {
		return context.getArgument(argumentName, PowerIdentifier.class);
	}
	
	public static PowerIdentifier getExistingPowerId(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
		PowerIdentifier id = getPowerId(context, argumentName);
		return PowerManager.getAsResult(id)
			.map(power -> id)
			.getOrThrow(err -> MiscUtil.createCommandException(() -> err));
	}

}
