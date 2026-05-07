package io.github.eggohito.neo_apoli.client.config.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.controllers.string.IStringController;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public record PatternController(Option<Pattern> option) implements IStringController<Pattern> {

	@Override
	public String getString() {
		return option().pendingValue().toString();
	}

	@Override
	public void setFromString(String value) {

		try {
			option().requestSet(Pattern.compile(value));
		}

		catch (PatternSyntaxException ignored) {

		}

	}

	@Override
	public boolean isInputValid(String input) {

		try {
			var ignored = Pattern.compile(input);
			return true;
		}

		catch (PatternSyntaxException ignored) {
			return false;
		}

	}

}
