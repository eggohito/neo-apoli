package io.github.eggohito.neo_apoli.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.PowerIdentifier;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class PowerArgumentType implements ArgumentType<PowerIdentifier> {

	private PowerArgumentType() {

	}

	@Override
	public PowerIdentifier parse(StringReader reader) throws CommandSyntaxException {
		return PowerIdentifier.fromCommandInput(reader);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestMatching(PowerManager.getIds().stream().map(PowerIdentifier::toString), builder);
	}

	public static PowerArgumentType power() {
		return new PowerArgumentType();
	}

	public static Power getPower(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
		PowerIdentifier powerId = context.getArgument(argumentName, PowerIdentifier.class);
		return PowerManager.getAsResult(powerId).getOrThrow(MiscUtil.PASSTHROUGH_COMMAND_EXCEPTION_TYPE::create);
	}

}
