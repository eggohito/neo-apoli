package io.github.eggohito.neo_apoli.exception;

import com.mojang.brigadier.exceptions.CommandExceptionType;

public class DummyCommandExceptionType implements CommandExceptionType {

	public static final DummyCommandExceptionType INSTANCE = new DummyCommandExceptionType();

	private DummyCommandExceptionType() {

	}

}
