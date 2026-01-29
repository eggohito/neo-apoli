package io.github.eggohito.neo_apoli.command.data.provider;

import io.github.eggohito.neo_apoli.api.event.DataProviderRegistration;
import net.minecraft.server.commands.data.DataCommands;

import java.util.function.Function;

public final class NeoApoliDataProviders {

    public static final Function<String, DataCommands.DataProvider> POWER_DATA = PowerDataProvider::new;

    public static void registerAll() {
        DataProviderRegistration.EVENT.register(registrant -> registrant.accept(POWER_DATA));
    }

}
