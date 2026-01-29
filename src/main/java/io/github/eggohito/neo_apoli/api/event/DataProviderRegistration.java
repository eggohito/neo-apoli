package io.github.eggohito.neo_apoli.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.commands.data.DataCommands;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 *  An event for registering {@linkplain net.minecraft.server.commands.data.DataCommands.DataProvider data providers},
 *  used for declaring a source of NBT data to modify/query from in {@code /execute store <result|success>}
 *  and {@code /data} commands.
 */
public interface DataProviderRegistration {

    Event<DataProviderRegistration> EVENT = EventFactory.createArrayBacked(
        DataProviderRegistration.class,
        callbacks -> registrant -> {

            for (var callback : callbacks) {
                callback.register(registrant);
            }

        }
    );

    void register(Consumer<Function<String, DataCommands.DataProvider>> registrant);

}
