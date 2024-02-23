package io.github.eggohito.neo_apoli.power.type;

import io.github.eggohito.neo_apoli.power.PowerType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.serialization.SerializableType;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import net.minecraft.registry.Registry;

public class PowerTypes {

    public static final IdentifierAlias ALIASES = new IdentifierAlias();

    public static void register() {
        register(SimplePowerType::getFactory);
        register(MultiplePowerType::getFactory);
    }

    public static void register(SerializableType.Supplier<PowerType> supplier) {
        SerializableType.Factory<? extends PowerType> factory = supplier.getFactory();
        Registry.register(NeoApoliRegistries.POWER_TYPES, factory.id(), factory.type());
    }

}
