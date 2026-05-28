package io.github.eggohito.neo_apoli.network;

import io.github.eggohito.neo_apoli.action.manager.ActionManager;
import io.github.eggohito.neo_apoli.condition.manager.ConditionManager;
import io.github.eggohito.neo_apoli.impl.misc.CommandStorageHolder;
import io.github.eggohito.neo_apoli.impl.misc.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.impl.power.PowersImpl;
import io.github.eggohito.neo_apoli.network.packet.clientbound.*;
import io.github.eggohito.neo_apoli.power.manager.PowerManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class NeoApoliClientboundPacketListener {

	public static void init() {

		ClientPlayConnectionEvents.INIT.register((handler, client) -> {
			ClientPlayNetworking.registerReceiver(ClientboundDismountEntityPacket.TYPE, (payload, context) -> payload.handle(context.player().level()));
			ClientPlayNetworking.registerReceiver(ClientboundMountEntityPacket.TYPE, (payload, context) -> payload.handle(context.player().level()));
			ClientPlayNetworking.registerReceiver(ActionManager.ClientboundActionsUpdatePacket.TYPE, (payload, context) -> payload.handle());
			ClientPlayNetworking.registerReceiver(ActionManager.ClientboundTagsUpdatePacket.TYPE, (payload, context) -> payload.handle());
			ClientPlayNetworking.registerReceiver(ConditionManager.ClientboundConditionsUpdatePacket.TYPE, (payload, context) -> payload.handle());
			ClientPlayNetworking.registerReceiver(ClientboundCommandStorageUpdatePacket.TYPE, (payload, context) -> payload.handle((CommandStorageHolder) context.client()));
			ClientPlayNetworking.registerReceiver(ClientboundPowerDataUpdatePacket.TYPE, (payload, context) -> payload.handle(context.player().level()));
			ClientPlayNetworking.registerReceiver(ClientboundPowerRecipeDisplaysUpdatePacket.TYPE, (payload, context) -> payload.handle((PowerRecipeDisplayHolder) context.client()));
			ClientPlayNetworking.registerReceiver(PowerManager.ClientboundPowersUpdatePacket.TYPE, (payload, context) -> payload.handle());
			ClientPlayNetworking.registerReceiver(PowerManager.ClientboundTagsUpdatePacket.TYPE, (payload, context) -> payload.handle());
			ClientPlayNetworking.registerReceiver(PowersImpl.GrantS2CPacket.TYPE, (payload, context) -> payload.handle(context.player().level()));
			ClientPlayNetworking.registerReceiver(PowersImpl.RevokeS2CPacket.TYPE,  (payload, context) -> payload.handle(context.player().level()));
		});

	}

}
