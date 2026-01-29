package io.github.eggohito.neo_apoli.client.integration.yacl.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.controllers.string.IStringController;

public record CharacterController(Option<Character> option) implements IStringController<Character> {

    @Override
    public String getString() {
        return String.valueOf(option().pendingValue());
    }

    @Override
    public void setFromString(String value) {
        option().requestSet(value.charAt(0));
    }

    @Override
    public boolean isInputValid(String input) {
        return input.length() == 1;
    }

}
