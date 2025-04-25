package io.github.eggohito.neo_apoli.registry;

import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class NeoApoliRegistries {

	public static final Registry<Power.Type<?>> POWER_TYPE = create(NeoApoliRegistryKeys.POWER_TYPE);

	public static final Registry<io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType<?>> ENTITY_CONDITION_TYPE = create(NeoApoliRegistryKeys.ENTITY_CONDITION_TYPE);
	public static final Registry<EntityActionType<?>> ENTITY_ACTION_TYPE = create(NeoApoliRegistryKeys.ENTITY_ACTION_TYPE);

	public static final Registry<NumberProvider.Type<?>> NUMBER_PROVIDER_TYPE = create(NeoApoliRegistryKeys.NUMBER_PROVIDER_TYPE);
	public static final Registry<StringProvider.Type<?>> STRING_PROVIDER_TYPE = create(NeoApoliRegistryKeys.STRING_PROVIDER_TYPE);

	private static <T> Registry<T> create(RegistryKey<Registry<T>> key) {
		return FabricRegistryBuilder.createSimple(key).buildAndRegister();
	}

}
