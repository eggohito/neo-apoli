package io.github.eggohito.neo_apoli.command.argument;

import io.github.eggohito.neo_apoli.NeoApoli;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;

public class NeoApoliArgumentTypes {

	public static void registerAll() {
		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("power"), PowerArgumentType.class, ConstantArgumentSerializer.of(PowerArgumentType::power));
	}

}
