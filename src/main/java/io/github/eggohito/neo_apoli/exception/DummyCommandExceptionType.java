package io.github.eggohito.neo_apoli.exception;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class DummyCommandExceptionType implements CommandExceptionType {

	public static final DummyCommandExceptionType INSTANCE = new DummyCommandExceptionType();

	private DummyCommandExceptionType() {

	}

	public CommandSyntaxException create(Message message) {
		return new CommandSyntaxException(this, message);
	}

	public CommandSyntaxException createWithContext(ImmutableStringReader reader, Message message) {
		return new CommandSyntaxException(this, message, reader.getString(), reader.getCursor());
	}

}
