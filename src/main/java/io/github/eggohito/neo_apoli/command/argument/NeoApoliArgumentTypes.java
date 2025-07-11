package io.github.eggohito.neo_apoli.command.argument;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.command.argument.category.ActionCategoryArgumentType;
import io.github.eggohito.neo_apoli.command.argument.category.ConditionCategoryArgumentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;

public class NeoApoliArgumentTypes {

	public static void registerAll() {

		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("power_reference"), PowerReferenceArgumentType.class, ConstantArgumentSerializer.of(PowerReferenceArgumentType::powerReference));

		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("action"), ActionArgumentType.class, new ActionArgumentType.Serializer());
		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("action_category"), ActionCategoryArgumentType.class, ConstantArgumentSerializer.of(ActionCategoryArgumentType::new));

		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("condition"), ConditionArgumentType.class, new ConditionArgumentType.Serializer());
		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("condition_category"), ConditionCategoryArgumentType.class, ConstantArgumentSerializer.of(ConditionCategoryArgumentType::new));

	}

}
