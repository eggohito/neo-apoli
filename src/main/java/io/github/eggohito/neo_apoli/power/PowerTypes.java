package io.github.eggohito.neo_apoli.power;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.registry.Registry;

public class PowerTypes {

	public static final PowerType<DummyPower> DUMMY = register("dummy", DummyPower.CODEC);
	public static final PowerType<MultiplePower> MULTIPLE = register("multiple", MultiplePower.CODEC);

	public static void registerAll() {

	}

	private static <P extends Power> PowerType<P> register(String path, MapCodec<P> codec) {
		return Registry.register(NeoApoliRegistries.POWER_TYPE, NeoApoli.id(path), new PowerType<>(codec));
	}

}
