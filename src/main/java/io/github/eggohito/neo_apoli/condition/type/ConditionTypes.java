package io.github.eggohito.neo_apoli.condition.type;

import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ConditionTypes {

	public static void registerAll() {
		BiEntityConditionTypes.registerAll();
		BlockConditionTypes.registerAll();
		DamageConditionTypes.registerAll();
		EntityConditionTypes.registerAll();
		ItemConditionTypes.registerAll();
		KeyConditionTypes.registerAll();
		MetaConditionTypes.registerAll();
	}

	protected static <C extends Condition, T extends ConditionType<C>> T register(Identifier id, T type) {
		return Registry.register(NeoApoliRegistries.CONDITION_TYPE, id, type);
	}

}
