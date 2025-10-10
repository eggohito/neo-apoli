package io.github.eggohito.neo_apoli.exception;

import java.util.function.Supplier;

public class AliasAlreadyTakenException extends RuntimeException {

	public <T> AliasAlreadyTakenException(T from, T to, Supplier<T> preExistingGetter) {
		super("Tried adding %s as an alias for %s, but it's already an alias for %s".formatted(from, to, preExistingGetter.get()));
	}

	public AliasAlreadyTakenException(String message) {
		super(message);
	}

}
