package io.github.eggohito.neo_apoli.client.integration.yacl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.AbstractControllerBuilderImpl;

public class CharacterControllerBuilder extends AbstractControllerBuilderImpl<Character> {

    public CharacterControllerBuilder(Option<Character> option) {
        super(option);
    }

    @Override
    public Controller<Character> build() {
        return new CharacterController(option);
    }

}
