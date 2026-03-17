package io.github.eggohito.neo_apoli.exception;

import java.util.function.UnaryOperator;

public class AliasAlreadyTakenException extends RuntimeException {

	public <T> AliasAlreadyTakenException(T from, T to, UnaryOperator<T> preExistingGetter) {
		super("Tried adding " + from + " as an alias for " + to + ", but it's already an alias for " + preExistingGetter.apply(from));
	}

	public AliasAlreadyTakenException(String message) {
		super(message);
	}

}
