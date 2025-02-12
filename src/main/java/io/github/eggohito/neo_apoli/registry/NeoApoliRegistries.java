package io.github.eggohito.neo_apoli.registry;

import io.github.eggohito.neo_apoli.power.PowerType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class NeoApoliRegistries {

	public static final Registry<PowerType<?>> POWER_TYPE = create(NeoApoliRegistryKeys.POWER_TYPE);

	private static <T> Registry<T> create(RegistryKey<Registry<T>> key) {
		return FabricRegistryBuilder.createSimple(key).buildAndRegister();
	}

}
