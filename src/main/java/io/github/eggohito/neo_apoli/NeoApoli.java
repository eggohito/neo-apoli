package io.github.eggohito.neo_apoli;

import io.github.eggohito.neo_apoli.action.type.ActionTypes;
import io.github.eggohito.neo_apoli.command.PowerCommand;
import io.github.eggohito.neo_apoli.command.argument.NeoApoliArgumentTypes;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import io.github.eggohito.neo_apoli.networking.packet.NeoApoliPackets;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeoApoli implements ModInitializer {

	public static final String MOD_NAMESPACE = "neo-apoli";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAMESPACE);

	@Override
	public void onInitialize() {

		CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> PowerCommand.register(commandDispatcher.getRoot()));
		NeoApoliArgumentTypes.registerAll();

		ValueProviderTypes.registerAll();

		ConditionTypes.registerAll();
		ActionTypes.registerAll();

		PowerTypes.registerAll();
		PowerManager.init();

		NeoApoliPackets.registerAll();

		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> NeoApoliEntityComponents.POWERS.get(entity).getPowers(true).forEach(power -> power.onAdded(entity)));
		ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> NeoApoliEntityComponents.POWERS.get(entity).getPowers(true).forEach(power -> power.onRemoved(entity)));

	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_NAMESPACE, path);
	}

}
