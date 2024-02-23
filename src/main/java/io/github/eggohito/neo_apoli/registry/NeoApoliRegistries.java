package io.github.eggohito.neo_apoli.registry;

import io.github.eggohito.neo_apoli.power.PowerType;
import io.github.eggohito.neo_apoli.serialization.SerializableType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;

public class NeoApoliRegistries {

    public static final Registry<SerializableType.Serializer<? extends PowerType>> POWER_TYPES;

    static {
        POWER_TYPES = FabricRegistryBuilder.createSimple(NeoApoliRegistryKeys.POWER_TYPES)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();
    }

}
