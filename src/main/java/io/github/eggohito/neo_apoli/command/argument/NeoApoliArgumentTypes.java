package io.github.eggohito.neo_apoli.command.argument;

import io.github.eggohito.neo_apoli.NeoApoli;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;

public class NeoApoliArgumentTypes {

	public static void registerAll() {

		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("power_reference"), PowerReferenceArgumentType.class, ConstantArgumentSerializer.of(PowerReferenceArgumentType::powerReference));

		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("action"), ActionArgumentType.class, new ActionArgumentType.Serializer());
		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("condition"), ConditionArgumentType.class, new ConditionArgumentType.Serializer());

	}

}
