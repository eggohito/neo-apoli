package io.github.eggohito.neo_apoli.util;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.eggohito.neo_apoli.exception.DummyCommandExceptionType;

public class MiscUtil {

	public static CommandSyntaxException createCommandException(Message message) {
		return DummyCommandExceptionType.INSTANCE.create(message);
	}

	public static CommandSyntaxException createCommandExceptionWithContext(ImmutableStringReader reader, Message message) {
		return DummyCommandExceptionType.INSTANCE.createWithContext(reader, message);
	}

}
