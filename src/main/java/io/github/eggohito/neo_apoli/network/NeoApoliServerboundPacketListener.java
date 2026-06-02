package io.github.eggohito.neo_apoli.network;

import io.github.eggohito.neo_apoli.action.manager.ActionManager;
import io.github.eggohito.neo_apoli.condition.manager.ConditionManager;
import io.github.eggohito.neo_apoli.impl.key.KeyStateManagerImpl;
import io.github.eggohito.neo_apoli.power.manager.PowerManager;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class NeoApoliServerboundPacketListener {

	public static void init() {

		ServerConfigurationNetworking.registerGlobalReceiver(ActionManager.ServerboundSyncAcknowledgedPacket.TYPE, ActionManager.ServerboundSyncAcknowledgedPacket::handle);
		ServerConfigurationNetworking.registerGlobalReceiver(ConditionManager.ServerboundSyncAcknowledgedPacket.TYPE, ConditionManager.ServerboundSyncAcknowledgedPacket::handle);
		ServerConfigurationNetworking.registerGlobalReceiver(PowerManager.ServerboundSyncAcknowledgedPacket.TYPE, PowerManager.ServerboundSyncAcknowledgedPacket::handle);

		ServerPlayConnectionEvents.INIT.register((listener, server) ->
			ServerPlayNetworking.registerReceiver(listener, KeyStateManagerImpl.ServerboundKeyStatesUpdatePacket.TYPE, (payload, context) -> payload.handle(context.player()))
		);

	}

}
