package io.github.eggohito.neo_apoli.registry;

import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerType;
import io.github.eggohito.neo_apoli.serialization.SerializableType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class NeoApoliRegistryKeys {

    public static final RegistryKey<Registry<Power>> POWERS;
    public static final RegistryKey<Registry<SerializableType.Serializer<? extends PowerType>>> POWER_TYPES;

    static {
        POWERS = RegistryKey.ofRegistry(Identifier.of("neo-apoli", "powers"));
        POWER_TYPES = RegistryKey.ofRegistry(Identifier.of("neo-apoli", "power_types"));
    }

}
